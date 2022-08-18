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
#define BCD(c) 5 * (5 * (5 * (5 * (5 * (5 * (5 * (5*(5*(c&512)+(c&256))+(c&128))+(c&64))+(c&32))+(c&16))+(c&8))+(c&4))+(c&2))+(c&1)
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
__device__ int isspace(unsigned char c) {
	return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f' || c == '\v';
}
__device__ char* trim(char* str) {
	size_t len = 0;
	char* frontp = str;
	char* endp = NULL;

	if (str == NULL) { return NULL; }
	if (str[0] == '\0') { return str; }
	for (int len = 0; str[len] != '\0'; len++) {
		if (str[len] != ' ') {
			endp = str + len;
			break;
		}
	}
	endp = str + len;

	/* Move the front and back pointers to address the first non-whitespace
	 * characters from each end.
	 */
	while (isspace((unsigned char)*frontp)) { ++frontp; }
	if (endp != frontp)
	{
		while (isspace((unsigned char)*(--endp)) && endp != frontp) {}
	}

	if (str + len - 1 != endp)
		*(endp + 1) = '\0';
	else if (frontp != str && endp == frontp)
		*str = '\0';

	/* Shift the string so that it starts at str so that if it's dynamically
	 * allocated, we can still free it on the returned pointer.  Note the reuse
	 * of endp to mean the front of the string buffer now.
	 */
	endp = str;
	if (frontp != str)
	{
		while (*frontp) { *endp++ = *frontp++; }
		*endp = '\0';
	}


	return str;
}


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

	//mycpy32(S, ctx->state);

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
	for (i = 0; i < 64; ++i) {
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

	// for each byte in message
	for (i = 0; i < len; ++i) {
		// ctx->data == message 512 bit chunk
		ctx->data[ctx->datalen] = data[i];
		ctx->datalen++;
		if (ctx->datalen == 64) {
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

	// Pad whatever data is left in the buffer.
	if (ctx->datalen < 56) {
		ctx->data[i++] = 0x80;
		while (i < 56)
			ctx->data[i++] = 0x00;
	}
	else {
		ctx->data[i++] = 0x80;
		while (i < 64)
			ctx->data[i++] = 0x00;
		sha256_transform(ctx, ctx->data);
		memset(ctx->data, 0, 56);
	}

	// Append to the padding the total message's length in bits and transform.
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

	// Since this implementation uses little endian byte ordering and SHA uses big endian,
	// reverse all the bytes when copying the final state to the output hash.
	for (i = 0; i < 4; ++i) {
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
__global__ void sha256_cuda(BYTE* data, int len, BYTE* result)
{
	//source: src_block
	// 　　　　不定長
	//result: nans&hash(sha256)
	//      2進化10進数整数(10byte)　32byte
	//　　　　↑固定長↑
	int i = blockIdx.x * blockDim.x + threadIdx.x;
	//文字列に乱数を挿入する。
	BYTE* mae = (BYTE*)malloc((len + 10) * sizeof(BYTE));
	mae = (BYTE*)malloc((len + 10) * sizeof(BYTE));
	int conma = 0;
	int index_ = 0;
	//,が２回出現したらその場所をindex_に保存してループを抜ける。
	for (index_ = 0; /*index_ < len*/; index_++)
	{
		mae[index_] = (BYTE)data[index_];
		if (data[index_] == ',')
		{
			conma += 1;
			if (conma == 2)
				break;
		}
	}
	curandState s;

	curand_init((unsigned long long)clock64() + i, 0, 0, &s);

	BYTE* str = (BYTE*)malloc(32);
	int rand = curand_uniform(&s) * 1000000000;
	//数字を文字列にしちゃうやつ。
	int da = 0;
	for (da = 0; rand != 0; da++)
	{
		int rem = rand % 10;
		str[da] = (BYTE)(rem + '0');
		rand = rand / 10;
	}

	int strlen = 0;
	for (strlen = 0; str[strlen] != '\0'; strlen++);
	//乱数の文字数を取得する。
	for (int a = 0; a < strlen; a++)
	{ // maeにstrをくっつける
		mae[index_ + a + 1] = str[a];
	}
	// maeに残りのdata[i]をくっつける。
	for (;/* index_ < len*/; index_++)
	{
		if (data[index_] != '\0') {
			mae[index_ + strlen + 1] = data[index_ + 1];
		}
		else {
			break;
		}
	}
	mae[index_ + strlen] = '\0';
	printf("%s←\n", mae);

	// help!!!!!
	SHA256_CTX ctx;
	sha256_init(&ctx);
	printf("%i\n", index_ + strlen);
	sha256_update(&ctx, mae, index_ + strlen);
	BYTE digest[64];
	for (int a = 0; a < 64; a++)
	{
		digest[a] = 0xff;
	}
	sha256_final(&ctx, (digest));
	//result: ,nans&hash(sha256),nans&hash(sha256)
	//TODO
	printf("%c\r\n",digest[0]);
	result[i * (16 + 2 + 9)] = ',';
	for (int a = i * (16 + 2 + 9) + 1, int b = 0; b < strlen; b++, a++) {
		result[a] = str[b];
	}
	result[i * (16 + 2 + 9)+strlen+1] = '&';
	for (int a = i * (16 + 4 + 9) + strlen + 2, int b = 0; b < 32; b++,a++) {
		result[a] = digest[b];
	}
}
int main(int argc, char** argv)
{
	//source: src_block
	// 　　　　不定長
	//result: hash(sha256)&nans
	//      2進化10進数整数(10byte)　32byte
	//　　　　↑固定長↑
	int threads = 10;
	cudaSetDevice(0);
	BYTE* data1 = reinterpret_cast<BYTE*>("00000193ab920406e5586e1a99472d573138e27191ca77ca897dc400f9abc8b8,b0bb15df4e3b489c5601b6a9c2d1aea66396b992653f547c22662a69e21bc8ec,,101,1657528305862");
	BYTE* dev_data;
	BYTE* dev_result_str;
	cudaMallocManaged((void**)&dev_data, 600 * sizeof(BYTE));
	cudaMallocManaged((void**)&dev_result_str, (32 + 2 + 9) * threads * sizeof(BYTE));
	cudaMemcpy(dev_data, data1, sizeof(BYTE) * 600, cudaMemcpyHostToDevice);
	sha256_cuda << <1, threads >> > (dev_data, 600, dev_result_str);
	cudaDeviceSynchronize();
	BYTE* result_str;
	result_str = (BYTE*)malloc((16 + 2 + 9) * threads * sizeof(BYTE));
	cudaMemcpy(result_str, dev_result_str, (16 + 2 + 9) * threads * sizeof(BYTE), cudaMemcpyDeviceToHost);
	for(int a = 0; a < (16 + 2 + 9) * threads; a++)
	{
		printf("%c", result_str[a]);
	}
	return 0;
}