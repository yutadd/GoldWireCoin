#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
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

#define checkCudaErrors(x) \
{ \
    cudaGetLastError(); \
    x; \
    cudaError_t err = cudaGetLastError(); \
    if (err != cudaSuccess) \
        printf("GPU: cudaError %d (%s)\n", err, cudaGetErrorString(err)); \
}
#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
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

#define checkCudaErrors(x) \
{ \
    cudaGetLastError(); \
    x; \
    cudaError_t err = cudaGetLastError(); \
    if (err != cudaSuccess) \
        printf("GPU: cudaError %d (%s)\n", err, cudaGetErrorString(err)); \
}
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