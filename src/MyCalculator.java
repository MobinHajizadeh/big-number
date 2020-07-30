import java.awt.*;
import java.awt.event.*;

public class MyCalculator extends Frame {
    public boolean setClear = true;
    StringBuilder input = new StringBuilder();
    char op;
    String digitButtonText[] = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0"};
    String operatorButtonText[] = {"*", "+", "-", "=", "^", "/"};
    String specialButtonText[] = {"C", "CE"};

    MyDigitButton digitButton[] = new MyDigitButton[digitButtonText.length];
    MyOperatorButton operatorButton[] = new MyOperatorButton[operatorButtonText.length];
    MySpecialButton specialButton[] = new MySpecialButton[specialButtonText.length];


    Label displayLabel = new Label("please enter C after every  ' = '", Label.RIGHT);
    Label memLabel = new Label(" ", Label.RIGHT);

    final int FRAME_WIDTH = 265, FRAME_HEIGHT = 350;
    final int HEIGHT = 40, WIDTH = 40, H_SPACE = 5, V_SPACE = 5;
    final int TOPX = 0, TOPY = 50;

    ///////////////////////////
    MyCalculator(String frameText)//constructor
    {
        super(frameText);
        int tempX = TOPX, y = TOPY;
        Color background_color = new Color(28, 28, 28);
        displayLabel.setBounds(tempX + 55, y, 165, HEIGHT);
        displayLabel.setBackground(background_color);
        displayLabel.setForeground(Color.WHITE);
        add(displayLabel);
        memLabel.setBounds(TOPX, TOPY + HEIGHT + V_SPACE, WIDTH, HEIGHT);
        add(memLabel);

//set Co-ordinates for Special Buttons  
        tempX = TOPX + 1 * (WIDTH + H_SPACE);
        y = TOPY + 1 * (HEIGHT + V_SPACE);
        Color special_button_color = new Color(212, 212, 210);
        for (int i = 0; i < specialButton.length; i++) {
            specialButton[i] = new MySpecialButton(tempX, y, WIDTH * 3 / 2, HEIGHT, specialButtonText[i], this);
            specialButton[i].setForeground(Color.BLACK);
            specialButton[i].setBackground(special_button_color);
            tempX = tempX + 2 * WIDTH + (H_SPACE - 15);
        }
//set Co-ordinates for Digit Buttons  
        int digitX = TOPX + WIDTH + H_SPACE;
        int digitY = TOPY + 2 * (HEIGHT + V_SPACE);
        tempX = digitX;
        y = digitY;
        Color digit_button_color = new Color(80, 80, 80);
        for (int i = 0; i < digitButton.length - 1; i++) {
            digitButton[i] = new MyDigitButton(tempX, y, WIDTH, HEIGHT, digitButtonText[i], this);
            digitButton[i].setForeground(Color.WHITE);
            digitButton[i].setBackground(digit_button_color);
            tempX += WIDTH + H_SPACE;
            if ((i + 1) % 3 == 0) {
                tempX = digitX;
                y += HEIGHT + V_SPACE;
            }
        }
        // set 0 button
        digitButton[digitButton.length - 1] = new MyDigitButton(tempX, y, WIDTH * 2 + H_SPACE, HEIGHT, digitButtonText[digitButton.length - 1], this);
        digitButton[digitButton.length - 1].setForeground(Color.WHITE);
        digitButton[digitButton.length - 1].setBackground(digit_button_color);

//set Co-ordinates for Operator Buttons  
        int opsX = digitX + 2 * (WIDTH + H_SPACE) + H_SPACE;
        int opsY = digitY;
        tempX = opsX;
        y = opsY;
        Color operator_button_color = new Color(255, 149, 0);
        for (int i = 0; i < operatorButton.length - 2; i++) {
            tempX += WIDTH + H_SPACE;
            operatorButton[i] = new MyOperatorButton(tempX, y, WIDTH, HEIGHT, operatorButtonText[i], this);
            operatorButton[i].setForeground(Color.WHITE);
            operatorButton[i].setBackground(operator_button_color);
            // if ((i + 1) % 2 == 0) {
            tempX = opsX;
            y += HEIGHT + V_SPACE;
            //}
        }
        // set ^
        operatorButton[4] = new MyOperatorButton(tempX - H_SPACE, y - WIDTH - V_SPACE, WIDTH, HEIGHT, operatorButtonText[4], this);
        operatorButton[4].setForeground(Color.WHITE);
        operatorButton[4].setBackground(operator_button_color);
        // set /
        operatorButton[5] = new MyOperatorButton(tempX + WIDTH + H_SPACE, TOPY + 1 * (HEIGHT + V_SPACE), WIDTH, HEIGHT, operatorButtonText[5], this);
        operatorButton[5].setForeground(Color.WHITE);
        operatorButton[5].setBackground(operator_button_color);


        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });
        setLayout(null);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setBackground(background_color);
        setVisible(true);
    }

    static String getFormattedText(String temp) {
        String resText = "" + temp;
        if (resText.lastIndexOf(".0") > 0)
            resText = resText.substring(0, resText.length() - 2);
        return resText;
    }

    ////////////////////////////////////////
    public static void main(String[] args) {
        new MyCalculator("M.H");
    }
}

/*******************************************/

class MyDigitButton extends Button implements ActionListener {
    MyCalculator cl;

    //////////////////////////////////////////
    MyDigitButton(int x, int y, int width, int height, String cap, MyCalculator clc) {
        super(cap);
        setBounds(x, y, width, height);
        this.cl = clc;
        this.cl.add(this);
        addActionListener(this);
    }

    /////////////////////////////////////////////////
    public void actionPerformed(ActionEvent ev) {
        String tempText = ((MyDigitButton) ev.getSource()).getLabel();
        StringBuilder index = new StringBuilder();
        index.append(tempText);
        if (cl.setClear) {
            cl.displayLabel.setText("" + index);
            cl.setClear = false;
        } else
            cl.displayLabel.setText(cl.displayLabel.getText() + index);
    }
}

/********************************************/

class MyOperatorButton extends Button implements ActionListener {
    MyCalculator cl;

    MyOperatorButton(int x, int y, int width, int height, String cap, MyCalculator clc) {
        super(cap);
        setBounds(x, y, width, height);
        this.cl = clc;
        this.cl.add(this);
        addActionListener(this);
    }

    ///////////////////////
    public void actionPerformed(ActionEvent ev) {
        String opText = ((MyOperatorButton) ev.getSource()).getLabel();
        cl.setClear = true;
        String temp = cl.displayLabel.getText();
        if (!opText.equals("=")) {
            cl.input.append(temp);
            cl.op = opText.charAt(0);
            cl.input.append(cl.op);
            return;
        }
        cl.input.append(temp);
        temp = (BigNumber.calculate(cl.input.toString())).toString();
        cl.displayLabel.setText(MyCalculator.getFormattedText(temp));
    }
}

/****************************************/
class MySpecialButton extends Button implements ActionListener {
    MyCalculator cl;

    MySpecialButton(int x, int y, int width, int height, String cap, MyCalculator clc) {
        super(cap);
        setBounds(x, y, width, height);
        this.cl = clc;
        this.cl.add(this);
        addActionListener(this);
    }

    //////////////////////////////////////////////////////////
    public void actionPerformed(ActionEvent ev) {
        String opText = ((MySpecialButton) ev.getSource()).getLabel();
//check for "C" button i.e. Reset  
        if (opText.equals("C")) {
            cl.op = ' ';
            cl.input.setLength(0);
            cl.memLabel.setText(" ");
        }

//it must be CE button pressed  
        cl.displayLabel.setText("0");
        cl.setClear = true;
    }
}