/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static  int L = 512;       // number of codewords = 2^W
    private static  int W = 9;         // codeword width
    public static void compress(String compType) { 

        int coded = 0;
        int uncoded = 0;
        double oldRatio =0.0;
        double newRatio = 0.0;
        double threshold =1.1;
        boolean firstChar = true;
        char type = compType.charAt(0);


        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.             
    
        if (type == 'n'){

            if (firstChar){
                BinaryStdOut.write(type);
                firstChar = false;
            }
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            else if (t < input.length() && W < 16)
               { W++;
                L = L+L;
                st.put(input.substring(0, t + 1), code++);
            }
            input = input.substring(t);            // Scan past s in input.
        } // END OF N TYPE

        if (type == 'm'){
            if (firstChar){
                BinaryStdOut.write(type);
                firstChar = false;
            }
            BinaryStdOut.write(st.get(s), W);
            int t = s.length();
            coded = coded + W;
            uncoded = uncoded + (8*t);
            newRatio = ((double)(uncoded)/(coded));
            if (oldRatio == 0)
                oldRatio =  ((double)(uncoded)/(coded));
            if ((oldRatio/newRatio) < threshold)
            {
                if (t < input.length() && code < L)    // Add s to symbol table.
                    st.put(input.substring(0, t + 1), code++);
                else if (t < input.length() && W < 16)
               { W++;
                L = L+L;
                st.put(input.substring(0, t + 1), code++);
            }
            }
    //SWAP ^ 
            if ((oldRatio/newRatio) >= threshold){
                st = new TST<Integer>();
                for (int i = 0; i < R; i++)
                    st.put("" + (char) i, i);
                W=9;
                L=512;
                oldRatio = 0.0;
                code = R+1;
                st.put(input.substring(0, t + 1), code++);
            }
            input = input.substring(t);
        }// END OF M TYPE

        if (type == 'r'){

            if (firstChar){
                BinaryStdOut.write(type);
                firstChar = false;
            }
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            else if (t < input.length() && W < 16)
               { W++;
                L = L+L;
                st.put(input.substring(0, t + 1), code++);
            }
            if (t < input.length() && W == 16 && L == (int)Math.pow(2,W) ){
                 st = new TST<Integer>();
                for (int i = 0; i < R; i++)
                    st.put("" + (char) i, i);
                W=9;
                L=512;
                oldRatio = 0.0;
                code = R+1;
                st.put(input.substring(0, t + 1), code++);
            }
            input = input.substring(t);
        }// END OF R TYPE



    }//WHILE
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
        String[] st = new String[(int)Math.pow(2, 16)];
        int i; // next available codeword value
        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF
        char expandType = BinaryStdIn.readChar();
        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];
        int coded = 0;
        int uncoded = 0;
        double oldRatio =0.0;
        double newRatio = 0.0;


        while (true) {

            if(expandType == 'n'){

                BinaryStdOut.write(val);
                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                String s = st[codeword];
                if (i == codeword) s = val + val.charAt(0);   // special case hack
                if (i < L-1) st[i++] = val + s.charAt(0);
                else if (W<16){
                    W++;
                    L= L+L;
                    st[i++] = val + s.charAt(0);
                    }
                val = s;
            }// end of N type
            
            if(expandType == 'm'){

                if (oldRatio == 0)
                    oldRatio =  ((double)(uncoded)/(coded));
                newRatio = ((double)(uncoded)/(coded));
                if ((oldRatio/newRatio) > 1.1)
                {

                oldRatio = 0.0;
                W=9;
                L=512;
                i=R+1;
                BinaryStdOut.write(val);
                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                String s = st[codeword];
                if (i == codeword) s = val + val.charAt(0);   // special case hack
                if (i < L-1) st[i++] = val + s.charAt(0);
                else if (W<16){
                    W++;
                    L= L+L;
                    st[i++] = val + s.charAt(0);
                    }
                val = s;
                }
                else {
                BinaryStdOut.write(val);
                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                String s = st[codeword];
                if (i == codeword) s = val + val.charAt(0);   // special case hack
                if (i < L-1) st[i++] = val + s.charAt(0);
                else if (W<16){
                    W++;
                    L= L+L;
                    st[i++] = val + s.charAt(0);
                    }
                val = s;
                }
                
            }// end of M type

            if(expandType == 'r'){
                
                if (W==16 && L==(int)Math.pow(2,W)){
                    W=9;
                    L=512;
                    i=R+1;
                    BinaryStdOut.write(val);
                    codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                    String s = st[codeword];
                if (i == codeword) s = val + val.charAt(0);   // special case hack
                if (i < L-1) st[i++] = val + s.charAt(0);
                else if (W<16){
                    W++;
                    L= L+L;
                    st[i++] = val + s.charAt(0);
                    }
                val = s;
                }
                else{
                    BinaryStdOut.write(val);
                    codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                    String s = st[codeword];
                if (i == codeword) s = val + val.charAt(0);   // special case hack
                if (i < L-1) st[i++] = val + s.charAt(0);
                else if (W<16){
                    W++;
                    L= L+L;
                    st[i++] = val + s.charAt(0);
                    }
                val = s;
                }
            }// end of R type
        }// end of while
        BinaryStdOut.close();
    }



   public static void main(String[] args) {
        if      (args[0].equals("-")) compress(args[1]);
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}