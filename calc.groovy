import javax.swing.*
import java.awt.*
import java.awt.event.*

class Calculator {
    def textField
    def stack = [] as Stack
    def lastNotNumberTyped = false

    Calculator() {
        initUI()
    }

    def initUI() {
        def frame = new JFrame(size: [200, 300], layout: new FlowLayout(), defaultCloseOperation: javax.swing.WindowConstants.EXIT_ON_CLOSE)

        textField = new TextField("0", 22)
        textField.setEnabled false
        frame.contentPane.add textField

        appendNumbersButtons frame
        appendDotButton frame
        // appendBracketsButtons frame
        appendOperatorsButtons frame

        frame.show()
    }

    def appendNumbersButtons(frame) {
        0.upto(9) {
            def number = it
            appendButtonWithAction(frame, "$number") { numberTyped(number) }
        }
    }

    def appendDotButton(frame) {
        appendButtonWithAction(frame, ".") { dotTyped() }
    }

    def appendBracketsButtons(frame) {
        for (bracket in ["(", ")"]) {
            def currentBracket = bracket
            appendButtonWithAction(frame, currentBracket) { /*TODO: Obsluga nawiasow*/ }
        }
    }

    def appendOperatorsButtons(frame) {
        for (operator in ["+", "-", "*", "/", "="]) {
            def currentOperator = operator
            appendButtonWithAction(frame, currentOperator) { operatorTyped(currentOperator) }
        }
    }

    def appendButtonWithAction(frame, buttonTitle, action) {
        def button = new JButton(buttonTitle)
        button.setPreferredSize(new Dimension(50, 30));
        frame.contentPane.add button
        button.addActionListener action
    }

    def numberTyped(number) {
        clearFieldIfNeeded()
        
        def text = textField.getText()
        if (text == "0") {
            textField.setText("$number")
        } else {
            textField.setText(text + "$number")
        }
    }

    def dotTyped() {
        clearFieldIfNeeded()

        def text = textField.getText()
        if (text.indexOf(".") < 0) {
            textField.setText(text + ".")
        }
    }

    def operatorTyped(operator) {
        lastNotNumberTyped = true
        def currentNumber = textField.getText() as double
        
        def result = calculate(operator, currentNumber)
        
        if (operator != "=") {
            stack.push result
            stack.push operator
        }
       
        textField.setText("" + result)
    }

    def calculate(currentOperator, currentNumber) {
        if (stack.isEmpty()) {
            return currentNumber
        }

        def previousOperator = stack.peek()

        if (priorityLowerEqual(currentOperator, previousOperator)) {
            previousOperator = stack.pop()
            def previousNumber = stack.pop()
            print previousNumber + " " + previousOperator + " " + currentNumber + "\n"
            
            def result = 0
            if (previousOperator == "+") {
                result = previousNumber + currentNumber
            }

            if (previousOperator == "-") {
                result = previousNumber - currentNumber
            }
            
            if (previousOperator == "*") {
                result = previousNumber * currentNumber
            }

            if (previousOperator == "/") {
                result = previousNumber / currentNumber
            }
            
            return calculate(currentOperator, result)
        } else {
            return currentNumber
        }
    }

    def priorityLowerEqual(currentOperator, previousOperator) {
        if (currentOperator == "=") {
            return true
        }

        if (currentOperator == previousOperator) {
            return true
        }

        // has same priority
        if ((currentOperator == "+" && previousOperator == "-") ||
            (currentOperator == "-" && previousOperator == "+") ||
            (currentOperator == "/" && previousOperator == "*") ||
            (currentOperator == "*" && previousOperator == "/")) {
                return true
        }
        
        // has lower priority
        if ((currentOperator == "+" && previousOperator == "*") ||
            (currentOperator == "-" && previousOperator == "*") || 
            (currentOperator == "+" && previousOperator == "/") ||
            (currentOperator == "-" && previousOperator == "/")) {
            return true
        }
        
        return false
    }

    def clearFieldIfNeeded() {
        if (lastNotNumberTyped) {
            textField.setText("0")
            lastNotNumberTyped = false
        }
    }
}

def calculator = new Calculator()
