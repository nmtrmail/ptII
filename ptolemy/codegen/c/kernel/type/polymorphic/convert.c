/*** convert_Array_Array() ***/
#define convert_Array_Array(a) a
/**/

/*** convert_Boolean_Array() ***/
inline Token convert_Boolean_Array(boolean a) {
  return $new(Array(1, 1, $new(Boolean(a)), TYPE_Boolean));
}
/**/

/*** convert_Boolean_Boolean() ***/
#define convert_Boolean_Boolean(a) a
/**/

/*** convert_Boolean_Int() ***/
inline int convert_Boolean_Int(boolean a) {
    return a ? 1 : 0;
}
/**/

/*** convert_Boolean_String() ***/
char* convert_Boolean_String(boolean a) {
    char *results;
    if (a) {
        // AVR does not have strdup
        results = (char*) malloc(sizeof(char) * 5);
        strcpy(results, "true");
    } else {
        results = (char*) malloc(sizeof(char) * 6);
        strcpy(results, "false");
    }
    return results;
}
/**/

/*** convert_DoubleArray_Array() ***/
Token convert_DoubleArray_Array(Token token) {
	int i;
	int length = token.payload.DoubleArray->size;
	Token result = $new(Array(length, 0));
	for (i = 0; i < length; i++) {
		Array_set(result, i, $convert_Double_Token(DoubleArray_get(token, i)));
	}
	return result;
}
/**/

/*** convert_DoubleArray_DoubleArray() ***/
#define convert_DoubleArray_DoubleArray(a) a
/**/

/*** convert_DoubleArray_IntArray() ***/
Token convert_DoubleArray_IntArray(Token token) {
	int i;
	int length = token.payload.DoubleArray->size;
	Token result = $new(IntArray(length, 0));
	for (i = 0; i < length; i++) {
		IntArray_set(result, i, (int) DoubleArray_get(token, i));
	}
	return result;
}
/**/

/*** convert_DoubleArray_StringArray() ***/
Token convert_DoubleArray_StringArray(Token token) {
	int i;
	int length = token.payload.DoubleArray->size;
	Token result = $new(StringArray(length, 0));
	for (i = 0; i < length; i++) {
		StringArray_set(result, i, $convert_Double_String(DoubleArray_get(token, i)));
	}
	return result;
}
/**/

/*** convert_Double_Array() ***/
inline Token convert_Double_Array(double a) {
  return $new(Array(1, 1, $new(Double(a)), TYPE_Double));
}
/**/

/*** convert_Double_Double() ***/
double convert_Double_Double(double a) {
    return a;
}
/**/

/*** convert_Double_DoubleArray() ***/
inline Token convert_Double_DoubleArray(double a) {
  return $new(DoubleArray(1, 1, a));
}
/**/

/*** convert_Double_Int() ***/
inline int convert_Double_Int(double a) {
    return (int) a;
}
/**/

/*** convert_Double_String() ***/
char* convert_Double_String(double a) {
    int index;
    char* string = (char*) malloc(sizeof(char) * 20);
    sprintf(string, "%.14g", a);

    // Make sure that there is a decimal point.
    if (strrchr(string, '.') == NULL) {
        index = strlen(string);
        if (index == 20) {
            string = (char*) realloc(string, sizeof(char) * 22);
        }
        string[index] = '.';
        string[index + 1] = '0';
        string[index + 2] = '\0';
    }
    return string;
}
/**/

/*** convert_Double_StringArray() ***/
Token convert_Double_StringArray(double d) {
	Token result = $new(StringArray(1, 0));
	StringArray_set(result, 0, $convert_Double_String(d));
	return result;
}
/**/

/*** convert_Double_Token() ***/
inline Token convert_Double_Token(double a) {
    return $new(Double(a));
}
/**/

/*** convert_IntArray_Array() ***/
Token convert_IntArray_Array(Token token) {
	int i;
	int length = token.payload.IntArray->size;
	Token result = $new(Array(length, 0));
	for (i = 0; i < length; i++) {
		Array_set(result, i, $convert_Int_Token(IntArray_get(token, i)));
	}
	return result;
}
/**/

/*** convert_IntArray_DoubleArray() ***/
Token convert_IntArray_DoubleArray(Token token) {
	int i;
	int length = token.payload.IntArray->size;
	Token result = $new(DoubleArray(length, 0));
	for (i = 0; i < length; i++) {
		DoubleArray_set(result, i, (double) IntArray_get(token, i));
	}
	return result;
}
/**/

/*** convert_IntArray_IntArray() ***/
#define convert_IntArray_IntArray(a) a
/**/

/*** convert_IntArray_StringArray() ***/
Token convert_IntArray_StringArray(Token token) {
	int i;
	int length = token.payload.IntArray->size;
	Token result = $new(StringArray(length, 0));
	for (i = 0; i < length; i++) {
		StringArray_set(result, i, $convert_Int_String(IntArray_get(token, i)));
	}
	return result;
}
/**/

/*** convert_IntArray_Token() ***/
#define convert_IntArray_Token(a) a
/**/

/*** convert_Int_Array() ***/
Token convert_Int_Array(int a) {
  return $new(Array(1, 1, $new(Int(a)), TYPE_Int));
}
/**/

/*** convert_Int_Boolean() ***/
inline boolean convert_Int_Boolean(int a) {
    return (a != 0) ? true : false;
}
/**/

/*** convert_Int_Double() ***/
inline double convert_Int_Double(int a) {
    return (double) a;
}
/**/

/*** convert_Int_Int() ***/
#define convert_Int_Int(a) a
/**/

/*** convert_Int_Long() ***/
long long convert_Int_Long(int a) {
    return (long long) a;
}
/**/

/*** convert_Int_String() ***/
char* convert_Int_String(int a) {
	// FIXME: should the string representation include the double quotes ""?

    char* string = (char*) malloc(sizeof(char) * 12);
    sprintf((char*) string, "%d", a);
    return string;
}
/**/

/*** convert_Int_StringArray() ***/
Token convert_Int_StringArray(int i) {
	Token result = $new(StringArray(1, 0));
	StringArray_set(result, 0, $convert_Int_String(i));
	return result;
}
/**/

/*** convert_Int_Token() ***/
Token convert_Int_Token(int a) {
    return $new(Int(a));
}
/**/

/*** convert_Long_Array() ***/
Token convert_Long_Array(long long a) {
  return $new(Array(1, 1, $new(Long(a)), TYPE_Long));
}
/**/

/*** convert_Long_Long() ***/
#define convert_Long_Long(a) a
/**/

/*** convert_Long_Token() ***/
inline Token convert_Long_Token(long long a) {
    return $new(Long(a));
}
/**/

/*** convert_Matrix_Matrix() ***/
inline Token convert_Matrix_Matrix(Token a1) {
    return a1;
}
/**/

/*** convert_StringArray_StringArray() ***/
#define convert_StringArray_StringArray(a) a
/**/

/*** convert_String_Array() ***/
inline Token convert_String_Array(char* a) {
  return $new(Array(1, 1, $new(String(a)), TYPE_String));
}
/**/

/*** convert_String_Boolean() ***/
char* convert_String_Boolean(char* a) {

}
/**/

/*** convert_String_Double() ***/
#define convert_String_Double atof
/**/

/*** convert_String_Int() ***/
#define convert_String_Int atoi
/**/

/*** convert_String_String() ***/
#define convert_String_String(a) a
/**/

/*** convert_String_StringArray() ***/
Token convert_String_StringArray(char* s) {
	Token result = $new(StringArray(1, 0));
	StringArray_set(result, 0, s);
	return result;
}
/**/

/*** convert_Token_Token() ***/
#define convert_Token_Token(a) a
/**/

