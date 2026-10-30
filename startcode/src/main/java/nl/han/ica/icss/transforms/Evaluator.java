package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
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
                transformStylerule((Stylerule) child);
            }

        }
        variableValues.removeFirst();
    }

    private void transformStylerule(Stylerule stylerule) {
        variableValues.addFirst(new HashMap<String, Literal>());
        ArrayList<ASTNode> children = stylerule.getChildren();
        for (ASTNode child : children) {
            if (child instanceof Declaration) {
                transformDeclaration((Declaration) child);
            }
            if (child instanceof IfClause) {
                transformIfClause((IfClause) child);
            }
            if (child instanceof VariableAssignment){
                assignVariable((VariableAssignment) child);
            }
        }
        variableValues.removeFirst();
    }

    private void transformDeclaration(Declaration declaration) {
        declaration.expression = transformExpression(declaration.expression);
    }

    private ArrayList<ASTNode> transformIfClause (IfClause ifClause){
        return new ArrayList<>();
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
