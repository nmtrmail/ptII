/***preinitBlock***/
static $targetType(output) $actorSymbol(state);
/**/

/*** ArrayConvertInitBlock($elementType) ***/
$actorSymbol(state) = $typeFunc(TYPE_Array::convert($actorSymbol(state), $elementType));
/**/

/*** ArrayConvertStepBlock($elementType) ***/
$ref(step) = $typeFunc(TYPE_Array::convert($ref(step), $elementType));
/**/


/***CommonInitBlock($type)***/
$actorSymbol(state) = $val(($type)init);
/**/

/***StringInitBlock***/
$actorSymbol(state) = strdup($val((String)init));
/**/

/***IntFireBlock***/
$ref(output) = $actorSymbol(state);
$actorSymbol(state) += $ref((Int)step);
/**/

/***DoubleFireBlock***/
$ref(output) = $actorSymbol(state);
$actorSymbol(state) += $ref((Double)step);
/**/

/***BooleanFireBlock***/
$ref(output) = $actorSymbol(state);
$actorSymbol(state) |= $ref((Boolean)step);
/**/

/***StringFireBlock***/
$ref(output) = (char*) realloc($ref(output), sizeof(char) * (strlen($actorSymbol(state)) + 1) );
strcpy($ref(output), $actorSymbol(state));
$actorSymbol(state) = (char*) realloc($actorSymbol(state), sizeof(char) * (strlen($actorSymbol(state)) + strlen($ref((String)step)) + 1) );
strcat($actorSymbol(state),  $ref((String)step));
/**/

/***TokenFireBlock***/
$ref(output) = $actorSymbol(state);
$actorSymbol(state) = $tokenFunc($ref(output)::add($ref((Token)step)));
/**/

/***declareBlock***/
#include <stdarg.h>     // Needed Matrix_new va_* macros

struct matrix {
    unsigned int row;            // number of rows.
    unsigned int column;         // number of columns.
    Token *elements;            // matrix of pointers to the elements. 
    //unsigned char elementsType;  // type of all the elements.
};

typedef struct matrix* MatrixToken;
/**/


/***funcDeclareBlock***/
Token Matrix_new(int row, int column, int given, ...);
Token Matrix_get(Token token, int row, int column) {   
    return token.payload.Matrix->elements[column * token.payload.Matrix->row + row];
}

void Matrix_set(Token matrix, int row, int column, Token element) {
    matrix.payload.Matrix->elements[column * matrix.payload.Matrix->row + row] = element;
}
/**/


/***deleteBlock***/
Token Matrix_delete(Token token, ...) { 
    int i, j;
	Token element, emptyToken;
      
    // Delete each elements.
    for (i = 0; i < token.payload.Matrix->column; i++) {
        for (j = 0; j < token.payload.Matrix->row; j++) {
            element = Matrix_get(token, j, i);
            functionTable[(int) element.type][FUNC_delete](element);
        }
    }
    free(token.payload.Matrix->elements);
    free(token.payload.Matrix);

    return emptyToken;     
}
/**/

/***convertBlock***/
Token Matrix_convert(Token token, ...) {
    /* token.payload.Matrix = (MatrixToken) malloc(sizeof(struct matrix));
       token.payload.Matrix->row = 1;
       token.payload.Matrix->column = 1;
       result.payload.Matrix->elements = (Token*) calloc(1, sizeof(Token));
       token.type = TYPE_Matrix;
       Matrix_set(token, 0, 0, token);
       return token;
    */
    return Matrix_new(1, 1, 1, token, token.type);
}
/**/


/***newBlock***/
// make a new matrix from the given values
// assume that number of the rest of the arguments == length,
// and they are in the form of (element, element, ...).
// The rest of the arguments should be of type Token *.
Token Matrix_new(int row, int column, int given, ...) {
    va_list argp; 
    int i;
    Token result;
    char elementType;

    result.type = TYPE_Matrix;
    result.payload.Matrix = (MatrixToken) malloc(sizeof(struct matrix));
    result.payload.Matrix->row = row;
    result.payload.Matrix->column = column;

    // Allocate a new matrix of Tokens.
    if (row > 0 && column > 0) {
        // Allocate an new 2-D array of Tokens.
        result.payload.Matrix->elements = (Token*) calloc(row * column, sizeof(Token));
    
        if (given > 0) {
            // Set the first element.
            va_start(argp, given);
            
            for (i = 0; i < given; i++) {
                result.payload.Matrix->elements[i] = va_arg(argp, Token);
            }    

            // elementType is given as the last argument.
            elementType = va_arg(argp, int);            

            if (elementType >= 0) {
                // convert the elements if needed.
                for (i = 0; i < given; i++) {
                    if (Matrix_get(result, i, 0).type != elementType) {
                        result.payload.Matrix->elements[i] = functionTable[(int)elementType][FUNC_convert](Matrix_get(result, i, 0));
                    }
                }    
            }

            va_end(argp);
        }
    }
    return result;
}    
/**/
    
/***equalsBlock***/
Token Matrix_equals(Token this, ...) {
    int i, j;
    va_list argp; 
    Token otherToken; 
    va_start(argp, this);
    otherToken = va_arg(argp, Token);
    va_end(argp);

    if (( this.payload.Matrix->row != otherToken.payload.Matrix->row ) ||
            ( this.payload.Matrix->column != otherToken.payload.Matrix->column )) {
        return Boolean_new(false);
    }
    for (i = 0; i < this.payload.Matrix->column; i++) { 
        for (j = 0; j < this.payload.Matrix->row; j++) { 
            if (!functionTable[(int) Matrix_get(this, j, i).type][FUNC_equals](Matrix_get(this, j, i), Matrix_get(otherToken, j, i)).payload.Boolean) {
                return Boolean_new(false);
            }
        }
    }
    return Boolean_new(true);
}
/**/


/***isCloseToBlock***/
Token Matrix_isCloseTo(Token this, ...) {
    int i, j;
    va_list argp; 
    Token otherToken; 
    Token tolerance;
    va_start(argp, this);
    otherToken = va_arg(argp, Token);
    tolerance = va_arg(argp, Token);

    if (( this.payload.Matrix->row != otherToken.payload.Matrix->row ) ||
            ( this.payload.Matrix->column != otherToken.payload.Matrix->column )) {
        return Boolean_new(false);
    }
    for (i = 0; i < this.payload.Matrix->column; i++) { 
        for (j = 0; j < this.payload.Matrix->row; j++) { 
            if (!functionTable[(int) Matrix_get(this, j, i).type][FUNC_isCloseTo](Matrix_get(this, j, i), Matrix_get(otherToken, j, i), tolerance).payload.Boolean) {
                return Boolean_new(false);
            }
        }
    }
    va_end(argp);
    return Boolean_new(true);
}
/**/

/***printBlock***/
Token Matrix_print(Token this, ...) {
    // Token string = Matrix_toString(this);
    // printf(string.payload.String);
    // free(string.payload.String);
    
    int i, j;
    printf("[");
    for (i = 0; i < this.payload.Matrix->column; i++) {
        if (i != 0) {
            printf("; ");
        }
        for (j = 0; j < this.payload.Matrix->row; j++) {
            if (j != 0) {
                printf(", ");
            }
            functionTable[this.payload.Matrix->elements[i * this.payload.Matrix->row + j].type][FUNC_print](this.payload.Matrix->elements[i]);
        }
    }
    printf("]");
}
/**/


/***toStringBlock***/
Token Matrix_toString(Token this, ...) {
    int i, j;
    int currentSize, allocatedSize;
    char* string;
    Token elementString;
    
    allocatedSize = 512;
    string = (char*) malloc(allocatedSize);
    string[0] = '[';
    string[1] = '\0';
    currentSize = 2;
    for (i = 0; i < this.payload.Matrix->column; i++) {
        if (i != 0) {
            strcat(string, "; ");
        }
        for (j = 0; j < this.payload.Matrix->row; j++) {
            if (j != 0) {
                strcat(string, ", ");
            }
            elementString = functionTable[(int) Matrix_get(this, j, i).type][FUNC_toString](Matrix_get(this, j, i));
            currentSize += strlen(elementString.payload.String);
            if (currentSize > allocatedSize) {
                allocatedSize *= 2;
                string = (char*) realloc(string, allocatedSize);
            }
            	
            strcat(string, elementString.payload.String);
            free(elementString.payload.String);
        }
    }
    strcat(string, "]");
    return String_new(string);
}
/**/



/***addBlock***/
// Assume the given otherToken is array type.
// Return a new Array token.
Token Matrix_add(Token this, ...) {
    int i, j;
    va_list argp; 
    Token result; 
    Token otherToken;
    
    va_start(argp, this);
    otherToken = va_arg(argp, Token);

    result = Matrix_new(this.payload.Matrix->row, this.payload.Matrix->column, 0);
    
    for (i = 0; i < this.payload.Matrix->column; i++) {
        for (j = 0; j < this.payload.Matrix->row; j++) {
            Matrix_set(result, j, i, functionTable[(int)Matrix_get(this, i, j).type][FUNC_add](Matrix_get(this, i, j), Matrix_get(otherToken, i, j)));
        }
    }

    va_end(argp);
    return result;
}
/**/


/***subtractBlock***/
// Assume the given otherToken is array type.
// Return a new Array token.
Token Matrix_subtract(Token this, ...) {
    int i, j;
    va_list argp; 
    Token result; 
    Token otherToken;
    
    va_start(argp, this);
    otherToken = va_arg(argp, Token);

    result = Matrix_new(this.payload.Matrix->row, this.payload.Matrix->column, 0);
    
    for (i = 0; i < this.payload.Matrix->column; i++) {
        for (j = 0; j < this.payload.Matrix->row; j++) {
            Matrix_set(result, j, i, functionTable[(int)Matrix_get(this, i, j).type][FUNC_subtract](Matrix_get(this, i, j), Matrix_get(otherToken, i, j)));
        }
    }

    va_end(argp);
    return result;
}
/**/




/***multiplyBlock***/
// Assume the given otherToken is array type.
// Return a new Array token.
Token Matrix_multiply(Token this, ...) {
    int i, j;
    va_list argp;
    Token result;
    Token element, otherToken;
    
    va_start(argp, this);
    otherToken = va_arg(argp, Token);
    if (otherToken.type == TYPE_Matrix
            && otherToken.payload.Matrix->row == 1
            && otherToken.payload.Matrix->column == 1) {
        // Handle simple scaling by a 1x1 matrix
        result = Matrix_new(this.payload.Matrix->row, this.payload.Matrix->column, 0);
    } else {
        result = Matrix_new(this.payload.Matrix->row, this.payload.Matrix->row, 0);
    }
    switch (otherToken.type) {
        case TYPE_Matrix:
        for (i = 0; i < this.payload.Matrix->column; i++) {
            for (j = 0; j < this.payload.Matrix->row; j++) {
                element = Matrix_get(this, j, i);
                if (otherToken.payload.Matrix->row == 1
                        && otherToken.payload.Matrix->column == 1) {
                    Matrix_set(result, j, i, functionTable[(int)element.type][FUNC_multiply](element, Matrix_get(otherToken, 0, 0)));
                }
            }
        }
        break;
        #ifdef TYPE_Array
        case TYPE_Array:
        element = Array_new(this.payload.Matrix->column *
        this.payload.Matrix->row, 0);
        for (i = 0; i < this.payload.Matrix->column; i++) {
            for (j = 0; j < this.payload.Matrix->row; j++) {
                Array_set(element,
                i + this.payload.Matrix->row * j,
                Matrix_get(this, j, i));
            }
        }
        break;
        #endif
        default:
        for (i = 0; i < this.payload.Matrix->column; i++) {
            for (j = 0; j < this.payload.Matrix->row; j++) {
                element = Matrix_get(this, j, i);
                result.payload.Matrix->elements[i] = functionTable[(int)element.type][FUNC_multiply](element, otherToken);
            }
        }
    }
    va_end(argp);
    return result;
}
/**/

/***divideBlock***/
// Assume the given otherToken is array type.
// Return a new Array token.
Token Matrix_divide(Token this, ...) {
    int i, j, index;
    va_list argp;
    Token result;
    Token element, otherToken;
    
    va_start(argp, this);
    otherToken = va_arg(argp, Token);
    
    switch (otherToken.type) {
        case TYPE_Matrix:
        for (i = 0; i < this.payload.Matrix->column; i++) {
            for (j = 0; j < this.payload.Matrix->row; j++) {
                element = Matrix_get(this, j, i);
                // FIXME: Need to program this.
            }
        }
        break;
        #ifdef TYPE_Array
        case TYPE_Array:
        // Divide reverse.
        result = Array_new(otherToken.payload.Array->size, 0);
        for (i = 0; i < otherToken.payload.Array->size; i++) {
            element = Array_get(this, i);
            result.payload.Array->elements[i] = functionTable[TYPE_Matrix][FUNC_divide](this, element);
        }
        
        break;
        #endif
        default:
        result = Matrix_new(this.payload.Matrix->row, this.payload.Matrix->column, 0);
        
        for (i = 0, index = 0; i < this.payload.Matrix->column; i++) {
            for (j = 0; j < this.payload.Matrix->row; j++, index++) {
                element = Matrix_get(this, j, i);
                result.payload.Matrix->elements[index] = functionTable[(int)element.type][FUNC_divide](element, otherToken);
            }
        }
    }
    va_end(argp);
    return result;
}
/**/

/***toExpressionBlock***/
Token Matrix_toExpression(Token this, ...) {
    return Matrix_toString(this);
}
/**/

