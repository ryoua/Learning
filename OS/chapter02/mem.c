//
//  mem.c
//  OS
//
//  Created by gusubaiyi on 2020/5/9.
//

#include "mem.h"

int main(int argc, char *argv[]) {
    int *p = malloc(sizeof(int));
    printf("(%d) memory address of p: %08x\n", getpid(), (unsigned) p);
    *p = 0;
    while (1) {
        sleep(1);
        *p = *p + 1;
        printf("(%d) p: %d\n", getpid(), *p);
        
    }
    return 0;
}