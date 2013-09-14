#include <stdio.h>
#include <string.h>
#include <stdlib.h>

  /* global data structures */

  struct tag_block { char tag; char sig[11]; unsigned size; };

  struct free_block { struct free_block *back_link, *fwd_link; };

  struct free_block *header;

  /* signature check macro */

  #define SIGCHK(w,x,y,z) {struct tag_block *scptr = (struct tag_block *)(w);\
  if(strncmp((char *)(scptr)+1,(x),(y))!=0){printf("*** sigchk fail\n");\
  printf("*** at %s, ptr is %p, sig is %s\n",(z),(w),(char *)(w)+1);}}

  #define TOPSIGCHK(a,b) {SIGCHK((a),"top_",4,(b))}
  #define ENDSIGCHK(a,b) {SIGCHK((a),"end_",4,(b))}

char *region_base;


/* routines */

void init_region(){
  int i;
  struct tag_block *ptr;
  struct free_block *links1,*links2;

  region_base = (char *)malloc(16480);

  ptr = (struct tag_block *)region_base;
  ptr->tag = 1;
  strcpy(ptr->sig,"end_region");
  ptr->size = 0;

  ptr = (struct tag_block *)(region_base + 16);
  ptr->tag = 0;
  strcpy(ptr->sig,"top_memblk");
  ptr->size = 16384;

  ptr = (struct tag_block *)(region_base + 16416);
  ptr->tag = 0;
  strcpy(ptr->sig,"end_memblk");
  ptr->size = 16384;

  ptr = (struct tag_block *)(region_base + 16432);
  ptr->tag = 1;
  strcpy(ptr->sig,"top_region");
  ptr->size = 0;

  links1 = (struct free_block *)(region_base + 32);
  links2 = (struct free_block *)(region_base + 16448);
  links1->back_link = links2;
  links1->fwd_link = links2;
  links2->back_link = links1;
  links2->fwd_link = links1;

  header = links2;

  printf("data structure starts at %p\n",region_base);
  printf("header is located at %p\n",header);
}

void prt_free_block(struct free_block *fb){
  struct tag_block *tb = (struct tag_block *)((char *)(fb) - 16);
  printf("   free block at %p of size 0x%x\n",(char *)fb,tb->size);
  TOPSIGCHK(tb,"prt_free_block")
  ENDSIGCHK((char *)(tb)+(tb->size)+16,"prt_free_block")
}

void prt_free_list(){
  struct free_block *ptr;
  if(header->fwd_link == header){
    printf("   ----------free list is empty-----------\n");
    return;
  }
  printf("   ---------------free list---------------\n");
  ptr = header->fwd_link;
  prt_free_block(ptr);
  ptr = ptr->fwd_link;
  while(ptr!=header){
    prt_free_block(ptr);
    ptr = ptr->fwd_link;
  }
  printf("   --------------end of list--------------\n");
}



/*
    void *alloc_mem(unsigned amount)
      input parameter
        amount is the number of bytes requested
      return value
        alloc_mem returns a pointer to the start of the allocated block of
        free memory, beyond the allocation information block.  note that
        the size of the allocation information block and the ending tag block
        is over and above the number of bytes requested in "amount".
      description
        alloc_mem searches for a block of free memory that can satisfy the
        requested amount of memory (and when necessary has enough extra space
        for an allocation information block).  when a suitable block is found,
        alloc_mem returns a pointer to the beginning of that memory (i.e.,
        immediately below the allocation information block).  if a suitable
        block cannot be found, alloc_mem returns a NULL pointer.  (note that
        "amount" should be rounded up to the nearest multiple of 16 bytes.)
*/
void *alloc_mem(unsigned amount);

/*
    void release_mem(void *ptr)
      input parameter
        ptr is a pointer to the start of the block of memory previously
        allocated; note that the allocation information block immediately
        precedes this address.
      return value
        0 if valid; nonzero if invalid
      description
        if the pointer yields a valid allocated block then the block is
        returned into the free list, possibly with compaction taking place.
        invalid pointers are reported with nonzero return code but no
        release actions are performed.
*/
unsigned release_mem(void *ptr);

int main(){
  void *ptr[20];
  unsigned rc;

  init_region();
  prt_free_list();

  printf("alloc 2048\n");
  ptr[0] = alloc_mem(2048); if(ptr[0]==NULL) printf("ptr[0] gets NULL\n");
  prt_free_list();
  printf("release 2048\n");
  rc=release_mem(ptr[0]); if(rc) printf("*** release_mem() fails\n");
  prt_free_list();

  printf("alloc 7 blocks\n");
  ptr[1] = alloc_mem(1024); if(ptr[1]==NULL) printf("ptr[1] gets NULL\n");
  ptr[2] = alloc_mem(1024); if(ptr[2]==NULL) printf("ptr[2] gets NULL\n");
  ptr[3] = alloc_mem(1024); if(ptr[3]==NULL) printf("ptr[3] gets NULL\n");
  ptr[4] = alloc_mem(1024); if(ptr[4]==NULL) printf("ptr[4] gets NULL\n");
  ptr[5] = alloc_mem(1024); if(ptr[5]==NULL) printf("ptr[5] gets NULL\n");
  ptr[6] = alloc_mem(1024);  if(ptr[6]==NULL) printf("ptr[6] gets NULL\n");
  ptr[7] = alloc_mem(1024);  if(ptr[7]==NULL) printf("ptr[7] gets NULL\n");
  prt_free_list();
  printf("release ptr[2]\n"); rc=release_mem(ptr[2]);
  if(rc) printf("*** release_mem() fails\n");
  printf("release ptr[4]\n"); rc=release_mem(ptr[4]);
  if(rc) printf("*** release_mem() fails\n");
  printf("release ptr[6]\n"); rc=release_mem(ptr[6]);
  if(rc) printf("*** release_mem() fails\n");
  prt_free_list();
  ptr[8] = alloc_mem(32);  if(ptr[8]==NULL) printf("ptr[8] gets NULL\n");
  ptr[9] = alloc_mem(256);  if(ptr[9]==NULL) printf("ptr[9] gets NULL\n");
  ptr[10] = alloc_mem(128);  if(ptr[10]==NULL) printf("ptr[10] gets NULL\n");
  prt_free_list();
  printf("release all\n");
  rc=release_mem(ptr[1]); if(rc) printf("*** release_mem() fails\n");
  rc=release_mem(ptr[3]); if(rc) printf("*** release_mem() fails\n");
  rc=release_mem(ptr[5]); if(rc) printf("*** release_mem() fails\n");
  rc=release_mem(ptr[7]); if(rc) printf("*** release_mem() fails\n");
  rc=release_mem(ptr[8]); if(rc) printf("*** release_mem() fails\n");
  rc=release_mem(ptr[9]); if(rc) printf("*** release_mem() fails\n");
  rc=release_mem(ptr[10]); if(rc) printf("*** release_mem() fails\n");
  prt_free_list();
  return 0;
}