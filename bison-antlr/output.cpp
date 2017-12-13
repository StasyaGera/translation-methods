#include "stdio.h"

int q;
int x;
int y;
int z;
int main() {
	x = 1;
	y = x;
	scanf("%d", &z);
	if (z == 1) {
		printf("%d, %d\n", z, 1);
	} else {
		if (z == 2) {
			printf("%d, %d\n", z, 2);
		} else {
			printf("%d\n", z);
		}
	}
	for (x = 1; x < 2; x++) {
		printf("%d\n", x);
	}
	scanf("%d", &q);
	while (q == 1) {
		printf("%d\n", q);
		q = 2;
	}
	return 0;
}
