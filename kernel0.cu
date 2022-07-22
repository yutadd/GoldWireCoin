#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <curand_kernel.h>
#include <curand.h>
#include <cuda.h>
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <stdio.h>
#include <vector>
#define SHA256_BLOCK_SIZE 32            // SHA256 outputs a 32 byte digest

#define ROTLEFT(a,b) (((a) << (b)) | ((a) >> (32-(b))))
#define ROTRIGHT(a,b) (((a) >> (b)) | ((a) << (32-(b))))

#define CH(x,y,z) (((x) & (y)) ^ (~(x) & (z)))
#define MAJ(x,y,z) (((x) & (y)) ^ ((x) & (z)) ^ ((y) & (z)))
#define EP0(x) (ROTRIGHT(x,2) ^ ROTRIGHT(x,13) ^ ROTRIGHT(x,22))
#define EP1(x) (ROTRIGHT(x,6) ^ ROTRIGHT(x,11) ^ ROTRIGHT(x,25))
#define SIG0(x) (ROTRIGHT(x,7) ^ ROTRIGHT(x,18) ^ ((x) >> 3))
#define SIG1(x) (ROTRIGHT(x,17) ^ ROTRIGHT(x,19) ^ ((x) >> 10))


/**************************** DATA TYPES ****************************/
typedef unsigned char BYTE;             // 8-bit byte
typedef uint32_t  WORD;             // 32-bit word, change to "long" for 16-bit machines

typedef struct JOB {
	BYTE* data;
	unsigned long long size;
	BYTE digest[64];
}JOB;


typedef struct {
	BYTE data[64];
	WORD datalen;
	unsigned long long bitlen;
	WORD state[8];
} SHA256_CTX;

__constant__ WORD dev_k[64];

static const WORD host_k[64] = {
	0x428a2f98,0x71374491,0xb5c0fbcf,0xe9b5dba5,0x3956c25b,0x59f111f1,0x923f82a4,0xab1c5ed5,
	0xd807aa98,0x12835b01,0x243185be,0x550c7dc3,0x72be5d74,0x80deb1fe,0x9bdc06a7,0xc19bf174,
	0xe49b69c1,0xefbe4786,0x0fc19dc6,0x240ca1cc,0x2de92c6f,0x4a7484aa,0x5cb0a9dc,0x76f988da,
	0x983e5152,0xa831c66d,0xb00327c8,0xbf597fc7,0xc6e00bf3,0xd5a79147,0x06ca6351,0x14292967,
	0x27b70a85,0x2e1b2138,0x4d2c6dfc,0x53380d13,0x650a7354,0x766a0abb,0x81c2c92e,0x92722c85,
	0xa2bfe8a1,0xa81a664b,0xc24b8b70,0xc76c51a3,0xd192e819,0xd6990624,0xf40e3585,0x106aa070,
	0x19a4c116,0x1e376c08,0x2748774c,0x34b0bcb5,0x391c0cb3,0x4ed8aa4a,0x5b9cca4f,0x682e6ff3,
	0x748f82ee,0x78a5636f,0x84c87814,0x8cc70208,0x90befffa,0xa4506ceb,0xbef9a3f7,0xc67178f2
};

/*********************** FUNCTION DECLARATIONS **********************/
char* print_sha(BYTE* buff);
extern __device__ void sha256_init(SHA256_CTX* ctx);
extern __device__ void sha256_update(SHA256_CTX* ctx, const BYTE data[], size_t len);
extern __device__ void sha256_final(SHA256_CTX* ctx, BYTE hash[]);
__global__ void sha256_cuda(char* data, int len, long long int* result_nans, BYTE* result){
	int i = blockIdx.x * blockDim.x + threadIdx.x;
	char* mae = "";
	int conma = 0;
	int index_ = 0;
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
	int da = 0;
	for (da = 0; rand != 0; da++)
	{
		int rem = rand % 10;
		str[da] = (char)(rem + '0');
		rand = rand / 10;
	}
	int srclen = 0;
	for (srclen = 0; str[srclen] != '\0'; srclen++);
	for (int a = 0; a < srclen; a++)
	{ 
		mae[index_ + a] = str[a];
	}
	for (index_ += srclen; index_ < sizeof(data[i]) / sizeof(char); index_++)
	{
		mae[index_ + srclen] = data[index_];
	}

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
