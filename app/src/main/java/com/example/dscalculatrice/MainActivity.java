package com.example.dscalculatrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    ListView historyCalcul;
    TextView txtcalcul;
    TextView txtresult;
    TextView caltextView;
    TextView resutldb;

    String calculs = "";
    String form ="";
    String temp ="";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTextViews();
        db = openOrCreateDatabase("DBCalculatice", MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS calculatrices ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "calculation TEXT,"
                + "result TEXT)");
        displayLastHistoryEntry();
    }
    private void initTextViews()
    {

        resutldb = (TextView)findViewById(R.id.resutldb);
        caltextView = (TextView)findViewById(R.id.caltextView);
        historyCalcul = (ListView) findViewById(R.id.historyCalcul);
        txtcalcul = (TextView)findViewById(R.id.txt_calcul);
        txtresult = (TextView)findViewById(R.id.txt_result);
    }


    private void setCalculs(String givenValue)
    {
        calculs = calculs + givenValue;
        txtcalcul.setText(calculs);
    }


    public void equalsOnClick(View view)
    {
        Double result = 0.0;
        try {
            if (calculs.endsWith("+") || calculs.endsWith("-") || calculs.endsWith("*") ||calculs.endsWith("/")) {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
                return;
            }
            if (calculs.equals("+") ||calculs.equals("-") || calculs.equals("*")||calculs.equals("*") ) {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
                return;
            }

            if (calculs != "" && calculs != null){
                result = eval(calculs);
                ContentValues values = new ContentValues();
                values.put("calculation", calculs);
                values.put("result", result);
                db.insert("calculatrices", null, values);
                displayLastHistoryEntry();
                if(result != null){
                    txtresult.setText(String.valueOf(result.doubleValue()));
                    calculs = "";
                }
            }

        } catch (Exception e)
        {
            Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isNumeric (char c)
    {
        if((c <= '9' && c >= '0') || c == '.')
            return true;

        return false;
    }


    public void clearOnClick(View view)
    {
        txtcalcul.setText("");
        calculs = "";
        txtresult.setText("");
        leftBracket = true;
    }

    boolean leftBracket = true;

    public void removeOnClick (View view)
    {

        String originalString = txtcalcul.getText().toString();
        calculs = originalString.substring(0, originalString.length() - 1);
        txtcalcul.setText(calculs);
    }


    public void divisionOnClick(View view)
    {
        if (calculs == "" || calculs == null) {
            Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            return;
        }
        setCalculs("/");
    }

    public void sevenOnClick(View view)
    {
        setCalculs("7");
    }

    public void eightOnClick(View view)
    {
        setCalculs("8");
    }

    public void nineOnClick(View view)
    {
        setCalculs("9");
    }

    public void timesOnClick(View view)
    {
        if (calculs == "" || calculs == null) {
            Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            return;
        }
        setCalculs("*");
    }

    public void fourOnClick(View view)
    {
        setCalculs("4");
    }

    public void fiveOnClick(View view)
    {
        setCalculs("5");
    }

    public void sixOnClick(View view)
    {
        setCalculs("6");
    }

    public void minusOnClick(View view)
    {
        if (calculs == "" || calculs == null) {
            Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            return;
        }
        setCalculs("-");
    }

    public void oneOnClick(View view)
    {
        setCalculs("1");
    }

    public void twoOnClick(View view)
    {
        setCalculs("2");
    }

    public void threeOnClick(View view)
    {
        setCalculs("3");
    }

    public void plusOnClick(View view)
    {
        if (calculs == "" || calculs == null) {
            Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            return;
        }
        setCalculs("+");
    }

    public void decimalOnClick(View view)
    {
        setCalculs(".");
    }

    public void zeroOnClick(View view)
    {
        setCalculs("0");
    }
    private void displayLastHistoryEntry() {

        Cursor cursor = db.rawQuery("SELECT id AS _id, calculation, result  FROM calculatrices ORDER BY id asc", null);
        if (cursor.moveToFirst()) {

           history adapter = new history(this, cursor, 0);
            historyCalcul.setAdapter(adapter);
            historyCalcul.setSelection(cursor.getCount()-1);

        }

    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected:  " + (char)ch);
                return x;
            }
            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            // | functionName `(` expression `)` | functionName factor
            // | factor `^` factor
            double parseExpression() {
                Double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return +parseFactor();
                else if (eat('-')) return -parseFactor(); // unary minus
                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')'  after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt"))
                        x = Math.sqrt(x);
                    else if (func.equals("sin"))
                        x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos"))
                        x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan"))
                        x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " +
                                func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
                return x;
            }
        }.parse();
    }
}