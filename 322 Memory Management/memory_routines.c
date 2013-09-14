// joe maley
// cpsc 322
// april 2 2013

#include "memory.h"

#define TAG_SIZE 16

void *alloc_mem(unsigned amount);
unsigned release_mem(void *ptr);

void *alloc_mem(unsigned amount) {

	// if there are no free blocks
	if (header->fwd_link == header) {
		return NULL;
	}

	// round to nearest positive multiple of 16 bytes
	if (amount % 16 != 0) {
		amount = amount + (16 - (amount % 16));
	}
	
	// find a suitible block
	struct free_block *temp = header->fwd_link;
	do {
		struct tag_block *top_memblk = ((void *)temp) - TAG_SIZE;

		if (top_memblk->size >= amount) {
			void *ret;
			
			// consume entire block
			if (top_memblk->size < amount + (TAG_SIZE * 3)) {
				top_memblk->tag = 1;
				
				struct tag_block *end_memblk = ((void *)temp) + top_memblk->size;
				end_memblk->tag = 1;
				
				// remove the link from free list
				struct free_block *temp_back = temp->back_link;
				struct free_block *temp_fwd = temp->fwd_link;
				temp_back->fwd_link = temp_fwd;
				temp_fwd->back_link = temp_back;
				
				memset((void *) temp, 0, top_memblk->size);
				ret = (void *) temp;
			}
			
			// free space left over
			else {
				top_memblk->size -= (amount + (TAG_SIZE * 2));

				struct tag_block *end_memblk = ((void *)top_memblk) + TAG_SIZE + top_memblk->size;
				end_memblk->tag = 0;
				strcpy(end_memblk->sig, "end_memblk");
				end_memblk->size = top_memblk->size;

				top_memblk = ((void *)end_memblk) + TAG_SIZE;
				top_memblk->tag = 1;
				strcpy(top_memblk->sig, "top_memblk");
				top_memblk->size = amount;
				
				end_memblk = ((void *)top_memblk) + TAG_SIZE + top_memblk->size;
				end_memblk->tag = 1;
				strcpy(end_memblk->sig, "end_memblk");
				end_memblk->size = top_memblk->size;
				
				memset((((void *) top_memblk) + TAG_SIZE), 0, top_memblk->size);
				ret = ((void *) top_memblk) + TAG_SIZE;
			}
			
			return ret;
		}
		
		temp = temp->fwd_link;
	} while (temp != header->fwd_link);
	
	return NULL;
}

unsigned release_mem(void *ptr) {
	struct tag_block *top_memblk = ptr - TAG_SIZE;
	if (strcmp("top_memblk", top_memblk->sig) != 0 || top_memblk->tag != 1) {
		return 1;
	}
	
	struct tag_block *end_memblk = ((void *)top_memblk) + TAG_SIZE + top_memblk->size;
	
	struct tag_block *above_end_memblk = ((void*)top_memblk) - TAG_SIZE;
	struct tag_block *below_top_memblk = ((void*)end_memblk) + TAG_SIZE;
    
	// case 1
	if (above_end_memblk->tag == 1 && below_top_memblk->tag == 1) {
		top_memblk->tag = 0;
		end_memblk->tag = 0;
		memset(ptr, 0, top_memblk->size);
		struct free_block *temp = ptr;
	
		int i = 0;
		struct free_block *target = header;
		do {
			target = target->fwd_link;
		} while (target->fwd_link != header
		&& (void *)target->fwd_link < ptr);
		
		temp->back_link = target;
		temp->fwd_link = target->fwd_link;
		
		temp->back_link->fwd_link = temp;
		temp->fwd_link->back_link = temp;
	}
	
	// case 2
	else if (above_end_memblk->tag == 0 && below_top_memblk->tag == 1) {
		struct tag_block *above_top_memblk = ((void *)above_end_memblk) - TAG_SIZE - above_end_memblk->size;
		int new_size = above_end_memblk->size + (TAG_SIZE * 2) + top_memblk->size;
		above_top_memblk->size = new_size;
		end_memblk->size = new_size;
		end_memblk->tag = 0;
		memset(((void *)above_top_memblk) + (TAG_SIZE * 2), 0, new_size - TAG_SIZE);
	}
	
	// case 3
	else if (above_end_memblk->tag == 1 && below_top_memblk->tag == 0) {
		struct tag_block *below_end_memblk = ((void *)below_top_memblk) + TAG_SIZE + below_top_memblk->size;
		
		int new_size = top_memblk->size + (TAG_SIZE * 2) + below_top_memblk->size;
		below_end_memblk->size = new_size;
		top_memblk->size = new_size;
		top_memblk->tag = 0;

		struct free_block *temp = header;
		struct free_block *new = ((void *) top_memblk) + TAG_SIZE;
		struct free_block *target = ((void *) below_top_memblk) + TAG_SIZE;
		
		new->fwd_link = target->fwd_link;
		new->back_link = target->back_link;
		
		do {
			struct free_block *old_fwd_link = temp->fwd_link;
			if (temp->fwd_link == target) {
				temp->fwd_link = new;
			}
				
			if (temp->back_link == target) {
				temp->back_link = new;
			}
			temp = old_fwd_link;
		} while (temp != header);
		
		memset(((void *)top_memblk) + (TAG_SIZE * 2), 0, new_size - TAG_SIZE);
	}
	
	// case 4
	else if (above_end_memblk->tag == 0 && below_top_memblk->tag == 0) {
		struct tag_block *above_top_memblk = ((void *)above_end_memblk) - TAG_SIZE - above_end_memblk->size;
		struct tag_block *below_end_memblk = ((void *)below_top_memblk) + TAG_SIZE + below_top_memblk->size;
		
		int new_size = above_top_memblk->size + (TAG_SIZE * 2) + top_memblk->size + (TAG_SIZE * 2) + below_top_memblk->size;
		above_top_memblk->size = new_size;
		below_end_memblk->size = new_size;
		
		struct free_block *above_free_block = ((void *)above_top_memblk) + TAG_SIZE;
		struct free_block *below_free_block = ((void *)below_top_memblk) + TAG_SIZE;
		
		above_free_block->fwd_link = below_free_block->fwd_link;
		below_free_block->fwd_link->back_link = above_free_block;
		
		memset(((void *)above_top_memblk) + (TAG_SIZE * 2), 0, new_size - TAG_SIZE);
	}
	return 0;
}