#include "stdio.h"

int x;
int y;
int z;
int main() {
	x = 1;
	y = x;
	scanf("%d", &z);
	if (z == 1) {
		printf("%d, %d", z, 1);
	} else {
		if (z == 2) {
			printf("%d, %d", z, 2);
		} else {
			printf("%d", z);
		}
	}
	return 0;
}
