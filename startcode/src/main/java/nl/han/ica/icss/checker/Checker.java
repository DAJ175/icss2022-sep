package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
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
            if (child instanceof ElseClause) {
                checkElseClause((ElseClause) child);
            }
        }
        variableTypes.removeFirst();
    }

    private void checkElseClause(ElseClause elseClause) {
        // methode bijna hetzelfde als checkIfClause
        variableTypes.addFirst(new HashMap<String, ExpressionType>());
        ArrayList<ASTNode> body = elseClause.body;
        variableTypes.removeFirst();
    }

    private void checkIfClause(IfClause ifClause) {
        variableTypes.addFirst(new HashMap<String, ExpressionType>());
        ArrayList<ASTNode> body = ifClause.body;
        variableTypes.removeFirst();
    }


    private void checkLiteral(Literal literal) {
        // ik weet nog niet wat ik hier moet checken
    }

    private ExpressionType getVariableReferenceType(VariableReference variableReference){
        for (HashMap<String, ExpressionType> scope : variableTypes){
            if (scope.containsKey(variableReference.name)){
                return scope.get(variableReference.name);
            }
        }
        return ExpressionType.UNDEFINED;
    }

    private void checkDeclaration(Declaration declaration) {
        // ik weet nog niet wat ik hier moet checken
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
        } else {
            expression.setError("Not Valid Assignment Syntax");
        }
    }
}
