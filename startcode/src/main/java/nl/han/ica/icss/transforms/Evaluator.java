package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();
        transformStylesheet(ast.root);
    }

    private void transformStylesheet(Stylesheet stylesheet) {
        variableValues.addFirst(new HashMap<String, Literal>());
        ArrayList<ASTNode> children = stylesheet.getChildren();
        for (ASTNode child : children) {
            if (child instanceof VariableAssignment) {
                assignVariable((VariableAssignment) child);
            }
            if (child instanceof Stylerule) {
                transformNodes(child.getChildren());
            }

        }
        variableValues.removeFirst();
    }

    private void transformNodes(ArrayList<ASTNode> children) {
        variableValues.addFirst(new HashMap<String, Literal>());
        ArrayList<ASTNode> newChildren = new ArrayList<>();
        for (ASTNode child : children) {
            if (child instanceof Declaration) {
                transformDeclaration((Declaration) child);
            }
            if (child instanceof IfClause) {
                transformIfClause((IfClause) child); // het is me niet gelukt om ifclause te transformeren
            }
            if (child instanceof VariableAssignment){
                assignVariable((VariableAssignment) child);
            }
        }
        children.clear();
        children.addAll(newChildren);
        variableValues.removeFirst();
    }

    private void transformDeclaration(Declaration declaration) {
        declaration.expression = transformExpression(declaration.expression);
    }

    private ArrayList<ASTNode> transformIfClause (IfClause ifClause){
        ArrayList<ASTNode> body;
        if (getBooleanValue(ifClause.conditionalExpression)){
            body = ifClause.body;
        } else if (ifClause.elseClause != null) {
            body = ifClause.elseClause.body;
        } else {
            body = new ArrayList<>();
        }
        transformNodes(body);
        return body;
    }

    private boolean getBooleanValue(Expression conditionalExpression) {
        if (conditionalExpression instanceof BoolLiteral){
            return ((BoolLiteral) conditionalExpression).value;
        }
        if (conditionalExpression instanceof VariableReference) {
            BoolLiteral literal = (BoolLiteral) getVariableReferenceLiteral((VariableReference) conditionalExpression);
            return literal.value;
        }
        return false;
    }

    private Literal transformExpression (Expression expression) {
        if (expression instanceof VariableReference) {
            expression = getVariableReferenceLiteral((VariableReference) expression);
        }
        if (expression instanceof Operation) {
            expression = getTotalOperationValue((Operation) expression);
        }
        return (Literal) expression;
    }

    private Literal getTotalOperationValue(Operation operation) {
        OperatorLiteral lhs = (OperatorLiteral) transformExpression(operation.lhs);
        OperatorLiteral rhs = (OperatorLiteral) transformExpression(operation.rhs);
        if (operation instanceof MultiplyOperation){
            lhs.value = lhs.value * rhs.value;
        }
        if (operation instanceof SubtractOperation){
            lhs.value = lhs.value - rhs.value;
        }
        if (operation instanceof AddOperation){
            lhs.value = lhs.value + rhs.value;
        }
        return lhs;
    }

    private Literal getVariableReferenceLiteral(VariableReference reference) {
        for (HashMap<String, Literal> scope : variableValues){
            if (scope.containsKey(reference.name)){
                return scope.get(reference.name);
            }
        }
        return null;
    }

    private void assignVariable(VariableAssignment node) {
        VariableReference reference = node.name;
        Literal variableValue = transformExpression(node.expression);
        variableValues.getFirst().put(reference.name, variableValue);
    }
}
