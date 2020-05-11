#include "t1.h"

static volatile int counter = 0;

void *my_thread(void *arg) {
    printf("%s: begin\n", (char *) arg);
    int i;
    for (int i = 0; i < 10000000; i++) {
        counter = counter + 1;
    }
    printf("%s: done\n", (char *) arg);
    return NULL;    
}

int main(int argc, char *argv[]) {
    pthread_t p1, p2;
    int rc;
    printf("main: begin (counter = %d) \n", counter);
    rc = pthread_create(&p1, NULL, my_thread, "A");
    assert(rc == 0);
    rc = pthread_create(&p2, NULL, my_thread, "B");
    assert(rc == 0);

    pthread_join(p1, NULL);
    pthread_join(p2, NULL);

    printf("main: end (counter = %d) \n", counter);
    return 0;
}