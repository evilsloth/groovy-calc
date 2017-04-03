import javax.swing.*
import java.awt.*
import java.awt.event.*

class Calculator {
    def textField
    def stack = new Stack()
    def stackOfstacks = new Stack()
    def lastNotNumberTyped = false

    Calculator() {
        initUI()
    }

    def initUI() {
        def frame = new JFrame(size: [200, 320], layout: new FlowLayout(), defaultCloseOperation: javax.swing.WindowConstants.EXIT_ON_CLOSE)

        textField = new TextField("0", 22)
        textField.setEnabled false
        frame.contentPane.add textField

        appendNumbersButtons frame
        appendDotButton frame
        appendBracketsButtons frame
        appendOperatorsButtons frame
        appendNegateButton frame
        appendClearButton frame

        frame.show()
    }

    def reset() {
        textField.setText "0"
        stack.clear()
        stackOfstacks.clear()
        lastNotNumberTyped = false
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
            appendButtonWithAction(frame, currentBracket) { bracketTyped(currentBracket) }
        }
    }

    def appendOperatorsButtons(frame) {
        for (operator in ["+", "-", "*", "/", "="]) {
            def currentOperator = operator
            appendButtonWithAction(frame, currentOperator) { operatorTyped(currentOperator) }
        }
    }

    def appendNegateButton(frame) {
        appendButtonWithAction(frame, "+/-") { negateTyped() }
    }

    def appendClearButton(frame) {
        appendButtonWithAction(frame, "C") { clearTyped() }
    }

    def appendButtonWithAction(frame, buttonTitle, action) {
        def button = new JButton(buttonTitle)
        button.setPreferredSize(new Dimension(50, 30));
        frame.contentPane.add button
        button.addActionListener action
    }

    def clearTyped() {
        reset()
    }

    def negateTyped() {
        def text = textField.getText()
        textField.setText (text[0] == "-" ? text.substring(1) : "-" + text)
    }

    def bracketTyped(bracket) {
        if (bracket == "(") {
            stackOfstacks.push(stack)
            stack = new Stack()
        } else if (bracket == ")") {
            operatorTyped("=")
            stack = stackOfstacks.pop()
        }
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
            def currentNumber = new BigDecimal(textField.getText())
            
            try {
            def result = calculate(operator, currentNumber)
            
            if (operator != "=") {
                stack.push result
                stack.push operator
            }
        
            textField.setText result.toPlainString()
        } catch (ArithmeticException e) {
            reset()
            textField.setText "Error: " + e.getMessage()
        }
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
                result = previousNumber.add currentNumber
            }

            if (previousOperator == "-") {
                result = previousNumber.subtract currentNumber
            }
            
            if (previousOperator == "*") {
                result = previousNumber.multiply currentNumber
            }

            if (previousOperator == "/") {
                result = previousNumber.divide currentNumber
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
