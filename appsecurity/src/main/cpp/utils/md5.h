
#ifndef HOHOLIDAY_MD5_H
#define HOHOLIDAY_MD5_H

#include <bitset>
#include <iostream>
#include <vector>

using namespace std;

/* Parameters of MD5. */
#define S11 7
#define S12 12
#define S13 17
#define S14 22
#define S21 5
#define S22 9
#define S23 14
#define S24 20
#define S31 4
#define S32 11
#define S33 16
#define S34 23
#define S41 6
#define S42 10
#define S43 15
#define S44 21

#define F(x, y, z) (((x) & (y)) | ((~x) & (z)))
#define G(x, y, z) (((x) & (z)) | ((y) & (~z)))
#define H(x, y, z) ((x) ^ (y) ^ (z))
#define I(x, y, z) ((y) ^ ((x) | (~z)))

#define ROTATELEFT(num, n) (((num) << (n)) | ((num) >> (32 - (n))))

/**
 * @Transformations for rounds 1, 2, 3, and 4.
 */
#define FF(a, b, c, d, x, s, ac)            \
    {                                       \
        (a) += F((b), (c), (d)) + (x) + ac; \
        (a) = ROTATELEFT((a), (s));         \
        (a) += (b);                         \
    }
#define GG(a, b, c, d, x, s, ac)            \
    {                                       \
        (a) += G((b), (c), (d)) + (x) + ac; \
        (a) = ROTATELEFT((a), (s));         \
        (a) += (b);                         \
    }
#define HH(a, b, c, d, x, s, ac)            \
    {                                       \
        (a) += H((b), (c), (d)) + (x) + ac; \
        (a) = ROTATELEFT((a), (s));         \
        (a) += (b);                         \
    }
#define II(a, b, c, d, x, s, ac)            \
    {                                       \
        (a) += I((b), (c), (d)) + (x) + ac; \
        (a) = ROTATELEFT((a), (s));         \
        (a) += (b);                         \
    }

typedef unsigned int bit32;

class MD5 {
public:
    MD5(const string &str);

    MD5();

    void init();

    static void showBinmsg(const vector<bool> &binmsg);

    const string getDigest();

    void padding();

    void sort_little_endian();

    void appendLength();

    void transform(int beginIndex);

    void decode(int beginIndex, bit32 *x);

    bit32 convertToBit32(const vector<bool> &a);

    const string to_str();

private:
    string input_msg;
    vector<bool> bin_msg;

    // b is the length of the original msg
    int b;
    vector<bool> bin_b;

    bit32 A, B, C, D;
};

#endif //HOHOLIDAY_MD5_H
