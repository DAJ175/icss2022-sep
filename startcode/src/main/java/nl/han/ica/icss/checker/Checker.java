package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        checkStyleSheet(ast.root);
    }

    public void checkStyleSheet(Stylesheet stylesheet){
        variableTypes.addFirst(new HashMap<String, ExpressionType>());
        ArrayList<ASTNode> children = stylesheet.getChildren();
        for(ASTNode child : children){
            if (child instanceof VariableAssignment){
                makeVariableAssignment((VariableAssignment) child);
            }
            if (child instanceof Stylerule){
                checkStylerule((Stylerule) child);
            }
        }
        variableTypes.removeFirst();
    }

    private void checkStylerule(Stylerule rule) {
        variableTypes.addFirst(new HashMap<String, ExpressionType>());
        ArrayList<ASTNode> children = rule.getChildren();
        for (ASTNode child : children) {
            if (child instanceof VariableAssignment){
                makeVariableAssignment((VariableAssignment) child);
            }
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
            if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            }
        }
        variableTypes.removeFirst();
    }

    private void checkDeclaration(Declaration declaration) {
        ArrayList<ExpressionType> AcceptedTypes = getPropertyTypes(declaration.property);
        ExpressionType declarationType = getExpressionType(declaration.expression);
        if (!AcceptedTypes.contains(declarationType)) {
            declaration.setError(declaration.expression.toString() + "does not match Property");
        }
    }

    private ExpressionType getExpressionType(Expression expression){
        if (expression instanceof Operation) {
            return getOperationType((Operation) expression);
        }
        if (expression instanceof Literal) {
            return getLiteralType((Literal) expression);
        }
        if (expression instanceof VariableReference) {
            return getVariableReferenceType((VariableReference) expression);
        }
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType getOperationType(Operation operation){
        checkOperationExpression(operation);
        ExpressionType operationType;
        ExpressionType lhsExpressionType = getExpressionType(operation.lhs);
        ExpressionType rhsExpressionType = getExpressionType(operation.rhs);
        if (lhsExpressionType == ExpressionType.SCALAR && rhsExpressionType == ExpressionType.SCALAR){
            operationType = ExpressionType.SCALAR;
        } else if (lhsExpressionType == ExpressionType.SCALAR ){
            operationType = rhsExpressionType;
        } else if (rhsExpressionType == ExpressionType.SCALAR){
            operationType = lhsExpressionType;
        } else {
            operationType = lhsExpressionType;
        }
        return operationType;
    }

    private void checkOperationExpression(Operation operation) {
        ExpressionType lhsExpressionType = ExpressionType.UNDEFINED;
        ExpressionType rhsExpressionType = ExpressionType.UNDEFINED;
        if (operation.lhs instanceof VariableReference) {
            lhsExpressionType = getVariableReferenceType((VariableReference) operation.lhs);
        }
        if (operation.lhs instanceof Literal) {
            lhsExpressionType = getLiteralType((Literal) operation.lhs);
        }
        if (operation.lhs instanceof Operation) {
            lhsExpressionType = getOperationType((Operation) operation.lhs);
        }
        if (operation.rhs instanceof VariableReference) {
            rhsExpressionType = getVariableReferenceType((VariableReference) operation.rhs);
        }
        if (operation.rhs instanceof Literal) {
            rhsExpressionType = getLiteralType((Literal) operation.rhs);
        }
        if (operation.rhs instanceof Operation) {
            rhsExpressionType = getOperationType((Operation) operation.rhs);
        }
        if (operation instanceof MultiplyOperation) {
            ArrayList<ExpressionType> mathables = new ArrayList<>();
            mathables.add(ExpressionType.PERCENTAGE);
            mathables.add(ExpressionType.PIXEL);
            mathables.add(ExpressionType.SCALAR);

            boolean validMultiplication = (rhsExpressionType == ExpressionType.SCALAR && mathables.contains(lhsExpressionType))
                    || (lhsExpressionType == ExpressionType.SCALAR && mathables.contains(rhsExpressionType));

            if (!validMultiplication){
                operation.setError("Input: " + operation.lhs + " and " + operation.rhs + " can not multiply");
            }
        } else {
            if (!(lhsExpressionType == rhsExpressionType)) {
                operation.setError("Input: " + operation.lhs + " and " + operation.rhs + " are not the same type");
            }
        }
    }

    private ExpressionType getLiteralType(Literal literal) {
        if (literal instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (literal instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (literal instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (literal instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (literal instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else {
            return ExpressionType.UNDEFINED;
        }
    }

    private ExpressionType getVariableReferenceType(VariableReference variableReference){
        for (HashMap<String, ExpressionType> scope : variableTypes){
            if (scope.containsKey(variableReference.name)){
                return scope.get(variableReference.name);
            }
        }
        variableReference.setError("Variable is not defined");
        return ExpressionType.UNDEFINED;
    }

    private ArrayList<ExpressionType> getPropertyTypes(PropertyName property) {
        ArrayList<ExpressionType> acceptedTypes = new ArrayList<>();
        switch (property.name){
            case "width":
            case "height":
                acceptedTypes.add(ExpressionType.PIXEL);
                acceptedTypes.add(ExpressionType.PERCENTAGE);
                acceptedTypes.add(ExpressionType.SCALAR);
                break;
            case "color":
            case "background-color":
                acceptedTypes.add(ExpressionType.COLOR);
                break;
            default:
                property.setError("Invalid property");
        }
        return acceptedTypes;
    }


    private void makeVariableAssignment(VariableAssignment variableAssignment) {
        VariableReference reference = variableAssignment.name;
        Expression expression = variableAssignment.expression;
        if (expression instanceof BoolLiteral) {
            variableTypes.getFirst().put(reference.name, ExpressionType.BOOL);
        } else if (expression instanceof ColorLiteral) {
            variableTypes.getFirst().put(reference.name, ExpressionType.COLOR);
        } else if (expression instanceof PercentageLiteral) {
            variableTypes.getFirst().put(reference.name, ExpressionType.PERCENTAGE);
        } else if (expression instanceof PixelLiteral) {
            variableTypes.getFirst().put(reference.name, ExpressionType.PIXEL);
        } else if (expression instanceof ScalarLiteral) {
            variableTypes.getFirst().put(reference.name, ExpressionType.SCALAR);
        } else if (expression instanceof VariableReference){
            for (HashMap<String, ExpressionType> scope : variableTypes){
                if (scope.containsKey(((VariableReference) expression).name)){
                    variableTypes.getFirst().put(reference.name,
                            scope.get(((VariableReference) expression).name));
                    return;
                }
            }
            expression.setError("Variable Not Found");
        } else if (expression instanceof Operation){
            checkOperationExpression((Operation) expression);
            variableTypes.getFirst().put(reference.name, getOperationType((Operation) expression));
        } else {
            expression.setError("Not Valid Assignment Syntax");
        }
    }

    private void checkBody(ArrayList<ASTNode> body) {
        for (ASTNode item : body) {
            if (item instanceof VariableAssignment){
                makeVariableAssignment((VariableAssignment) item);
            }
            if (item instanceof Declaration) {
                checkDeclaration((Declaration) item);
            }
            if (item instanceof IfClause) {
                checkIfClause((IfClause) item);
            }
        }
    }

    private void checkElseClause(ElseClause elseClause) {
        variableTypes.addFirst(new HashMap<String, ExpressionType>());
        checkBody(elseClause.body);
        variableTypes.removeFirst();
    }

    private void checkIfClause(IfClause ifClause) {
        variableTypes.addFirst(new HashMap<String, ExpressionType>());
        if (ifClause.conditionalExpression == null ) {
            ifClause.setError("No conditionalExpression found");
        }
        if (ifClause.conditionalExpression instanceof VariableReference){
            if (getVariableReferenceType((VariableReference) ifClause.conditionalExpression) != ExpressionType.BOOL) {
                ifClause.setError("ConditionalExpression is not a boolean");
            }
        }
        if (ifClause.conditionalExpression instanceof Literal) {
            if (getLiteralType((Literal) ifClause.conditionalExpression) != ExpressionType.BOOL){
                ifClause.setError("ConditionalExpression is not a boolean");
            }
        }

        checkBody(ifClause.body);

        if (ifClause.elseClause != null) {
            checkElseClause(ifClause.elseClause);
        }
        variableTypes.removeFirst();
    }
}
