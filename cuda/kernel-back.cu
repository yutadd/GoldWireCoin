
#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include <assert.h>
#include <stdio.h>
#include <cuda.h>
#include <stdio.h>
// include curand
#include <curand_kernel.h>
#include <curand.h>
#include <stdlib.h>
#include <ctype.h>
#include <stdio.h>
#include <vector>
#define SHA256_BLOCK_SIZE 32 // SHA256 outputs a 32 byte digest

#define ROTLEFT(a, b) (((a) << (b)) | ((a) >> (32 - (b))))
#define ROTRIGHT(a, b) (((a) >> (b)) | ((a) << (32 - (b))))

#define CH(x, y, z) (((x) & (y)) ^ (~(x) & (z)))
#define MAJ(x, y, z) (((x) & (y)) ^ ((x) & (z)) ^ ((y) & (z)))
#define EP0(x) (ROTRIGHT(x, 2) ^ ROTRIGHT(x, 13) ^ ROTRIGHT(x, 22))
#define EP1(x) (ROTRIGHT(x, 6) ^ ROTRIGHT(x, 11) ^ ROTRIGHT(x, 25))
#define SIG0(x) (ROTRIGHT(x, 7) ^ ROTRIGHT(x, 18) ^ ((x) >> 3))
#define SIG1(x) (ROTRIGHT(x, 17) ^ ROTRIGHT(x, 19) ^ ((x) >> 10))

/**************************** DATA TYPES ****************************/
typedef unsigned char BYTE; // 8-bit byte
typedef uint32_t WORD;		// 32-bit word, change to "long" for 16-bit machines

typedef struct JOB
{
	BYTE* data;
	unsigned long long size;
	BYTE digest[64];
} JOB;

typedef struct
{
	BYTE data[64];
	WORD datalen;
	unsigned long long bitlen;
	WORD state[8];
} SHA256_CTX;

__constant__ WORD dev_k[64];

static const WORD host_k[64] = {
	0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
	0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
	0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
	0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
	0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
	0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
	0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
	0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2 };

/*********************** FUNCTION DECLARATIONS **********************/
char* print_sha(BYTE* buff);
__device__ void sha256_init(SHA256_CTX* ctx);
__device__ void sha256_update(SHA256_CTX* ctx, const BYTE data[], size_t len);
__device__ void sha256_final(SHA256_CTX* ctx, BYTE hash[]);

char* hash_to_string(BYTE* buff)
{
	char* string = (char*)malloc(70);
	int k, i;
	for (i = 0, k = 0; i < 32; i++, k += 2)
	{
		sprintf(string + k, "%.2x", buff[i]);
		// printf("%02x", buff[i]);
	}
	string[64] = 0;
	return string;
}

__device__ void sha256_transform(SHA256_CTX* ctx, const BYTE data[])
{
	WORD a, b, c, d, e, f, g, h, i, j, t1, t2, m[64];
	WORD S[8];

	// mycpy32(S, ctx->state);

#pragma unroll 16
	for (i = 0, j = 0; i < 16; ++i, j += 4)
		m[i] = (data[j] << 24) | (data[j + 1] << 16) | (data[j + 2] << 8) | (data[j + 3]);

#pragma unroll 64
	for (; i < 64; ++i)
		m[i] = SIG1(m[i - 2]) + m[i - 7] + SIG0(m[i - 15]) + m[i - 16];

	a = ctx->state[0];
	b = ctx->state[1];
	c = ctx->state[2];
	d = ctx->state[3];
	e = ctx->state[4];
	f = ctx->state[5];
	g = ctx->state[6];
	h = ctx->state[7];

#pragma unroll 64
	for (i = 0; i < 64; ++i)
	{
		t1 = h + EP1(e) + CH(e, f, g) + dev_k[i] + m[i];
		t2 = EP0(a) + MAJ(a, b, c);
		h = g;
		g = f;
		f = e;
		e = d + t1;
		d = c;
		c = b;
		b = a;
		a = t1 + t2;
	}

	ctx->state[0] += a;
	ctx->state[1] += b;
	ctx->state[2] += c;
	ctx->state[3] += d;
	ctx->state[4] += e;
	ctx->state[5] += f;
	ctx->state[6] += g;
	ctx->state[7] += h;
}

__device__ void sha256_init(SHA256_CTX* ctx)
{
	ctx->datalen = 0;
	ctx->bitlen = 0;
	ctx->state[0] = 0x6a09e667;
	ctx->state[1] = 0xbb67ae85;
	ctx->state[2] = 0x3c6ef372;
	ctx->state[3] = 0xa54ff53a;
	ctx->state[4] = 0x510e527f;
	ctx->state[5] = 0x9b05688c;
	ctx->state[6] = 0x1f83d9ab;
	ctx->state[7] = 0x5be0cd19;
}

__device__ void sha256_update(SHA256_CTX* ctx, const BYTE data[], size_t len)
{
	WORD i;
	for (i = 0; i < len; ++i)
	{
		ctx->data[ctx->datalen] = data[i];
		ctx->datalen++;
		if (ctx->datalen == 64)
		{
			sha256_transform(ctx, ctx->data);
			ctx->bitlen += 512;
			ctx->datalen = 0;
		}
	}
}

__device__ void sha256_final(SHA256_CTX* ctx, BYTE hash[])
{
	WORD i;

	i = ctx->datalen;
	if (ctx->datalen < 56)
	{
		ctx->data[i++] = 0x80;
		while (i < 56)
			ctx->data[i++] = 0x00;
	}
	else
	{
		ctx->data[i++] = 0x80;
		while (i < 64)
			ctx->data[i++] = 0x00;
		sha256_transform(ctx, ctx->data);
		memset(ctx->data, 0, 56);
	}
	ctx->bitlen += ctx->datalen * 8;
	ctx->data[63] = ctx->bitlen;
	ctx->data[62] = ctx->bitlen >> 8;
	ctx->data[61] = ctx->bitlen >> 16;
	ctx->data[60] = ctx->bitlen >> 24;
	ctx->data[59] = ctx->bitlen >> 32;
	ctx->data[58] = ctx->bitlen >> 40;
	ctx->data[57] = ctx->bitlen >> 48;
	ctx->data[56] = ctx->bitlen >> 56;
	sha256_transform(ctx, ctx->data);

	for (i = 0; i < 4; ++i)
	{
		hash[i] = (ctx->state[0] >> (24 - i * 8)) & 0x000000ff;
		hash[i + 4] = (ctx->state[1] >> (24 - i * 8)) & 0x000000ff;
		hash[i + 8] = (ctx->state[2] >> (24 - i * 8)) & 0x000000ff;
		hash[i + 12] = (ctx->state[3] >> (24 - i * 8)) & 0x000000ff;
		hash[i + 16] = (ctx->state[4] >> (24 - i * 8)) & 0x000000ff;
		hash[i + 20] = (ctx->state[5] >> (24 - i * 8)) & 0x000000ff;
		hash[i + 24] = (ctx->state[6] >> (24 - i * 8)) & 0x000000ff;
		hash[i + 28] = (ctx->state[7] >> (24 - i * 8)) & 0x000000ff;
	}
}

#define checkCudaErrors(x)                                                    \
	{                                                                         \
		cudaGetLastError();                                                   \
		x;                                                                    \
		cudaError_t err = cudaGetLastError();                                 \
		if (err != cudaSuccess)                                               \
			printf("GPU: cudaError %d (%s)\n", err, cudaGetErrorString(err)); \
	}
__global__ void sha256_cuda(char* data, int len, long long int* result_nans, BYTE* result)
{
	int i = blockIdx.x * blockDim.x + threadIdx.x;
	//文字列に乱数を挿入する。
	char* mae = "";
	int conma = 0;
	int index_ = 0;
	//,が２回出現したらその場所をindex_に保存してループを抜ける。
	for (index_ = 0; index_ < len; index_++)
	{
		mae[index_] = (char)data[index_];
		if (data[index_] == ',')
		{
			conma += 1;
			if (conma == 2)
				break;
		}
	}
	curandState s;

	curand_init((unsigned long long)clock64() + i, 0, 0, &s);

	char* str = (char*)malloc(32);
	int rand = curand_uniform(&s) * 1000000000;
	result_nans[i] = rand;
	//数字を文字列にしちゃうやつ。
	int da = 0;
	for (da = 0; rand != 0; da++)
	{
		int rem = rand % 10;
		str[da] = (char)(rem + '0');
		rand = rand / 10;
	}
	int srclen = 0;
	for (srclen = 0; str[srclen] != '\0'; srclen++);
	//乱数の文字数を取得する。
	for (int a = 0; a < srclen; a++)
	{ // maeにsrcをくっつける
		mae[index_ + a] = str[a];
	}
	// maeに残りのdata[i]をくっつける。
	for (index_ += srclen; index_ < sizeof(data[i]) / sizeof(char); index_++)
	{
		mae[index_ + srclen] = data[index_];
	}
	// help!!!!!

	SHA256_CTX ctx;
	sha256_init(&ctx);
	sha256_update(&ctx, (unsigned char*)mae, len + srclen);
	BYTE* digest = (BYTE*)malloc(64 * sizeof(BYTE));
	for (int a = 0; a < 64; a++)
	{
		digest[a] = 0xff;
	}

	sha256_final(&ctx, (digest));
	char* string = (char*)malloc(70);
	int k;
	for (int a = 0, k = 0; a < 32; a++, k += 2)
	{
		string[k] = (char)digest[a];
	}

	
	for (int a = 0; a<70;a++) {
		result[a] = string[a];
	}
}
int main(int argc, char** argv)
{
	cudaSetDevice(0);
	char* data1 = "previous_hash,addr,,1";
	char* dev_data;
	BYTE* dev_result_str;
	long long int* dev_result;
	cudaMallocManaged((void**)&dev_data, 100 * sizeof(char));
	cudaMallocManaged((void**)&dev_result, sizeof(long long int));
	cudaMallocManaged((void**)&dev_result_str, sizeof(char*) * 70);
	cudaMemcpy(dev_data, data1, 100 * sizeof(char), cudaMemcpyHostToDevice);
	char* buff = (char*)malloc(sizeof(long long int));
	sha256_cuda << <1, 1 >> > (dev_data, 22, dev_result, dev_result_str);
	cudaDeviceSynchronize();
	long long int *result;

	result = (long long int *)malloc(1 * sizeof(long long int));
	cudaMemcpy(result, dev_result, sizeof(long long int), cudaMemcpyDeviceToHost);
	printf("%lld\n", result[0]);
	BYTE* result_str;
	result_str = (BYTE*)malloc(70);
	cudaMemcpy(result_str, dev_result_str, 70, cudaMemcpyDeviceToHost);
	printf( hash_to_string(result_str));
	return 0;
}