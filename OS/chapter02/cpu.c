//
//  cpu.c
//  OS
//
//  Created by gusubaiyi on 2020/5/9.
//

#include "cpu.h"

int main(int argc, char *argv[]) {
    if (argc != 2) {
        fprintf(stderr, "usage: cpu<string>\n");
        exit(1);
    }
    char *str = argv[1];
    while (1) {
        sleep(1);
        printf("%s\n", str);
    }
    return 0;
}


for(优惠券)