#include <stdio>

void initcbuf(void);
void put(char);
char get(void);
void printbuf(void);

int main(void){
  char c,str[80];
  initcbuf();

	strcpy(str, "");
	while('q' != str[0]){
		printf("enter action (p=put,g=get,y=prynt,q=quit): ");
		  scanf("%s",str);

		switch(str[0]) {
			case 'p':
				printf("  enter character to put: ");
				scanf("%s",str);
				put(str[0]);
				break;
			case 'g' :
				c = get();
				if(!c) {
					printf("  can't get from empty buffer.\n");
				}
				else {
					printf("  character removed is: %c\n",c);
				}
				break;
			case 'y' :
				printbuf();
				break;
			case 'q' :
				break;
			default:
				printf("  unrecognized action\n");
				break;
		}
	}

	return 0;
}
