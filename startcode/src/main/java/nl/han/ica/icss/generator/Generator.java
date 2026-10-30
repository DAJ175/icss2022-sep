package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

import java.util.ArrayList;

public class Generator {
	StringBuilder stringBuilder;
	int scope = 0;

	public String generate(AST ast) {
		stringBuilder = new StringBuilder();
		generateStylesheet(ast.root);
        return stringBuilder.toString();
	}

	private void generateStylesheet(Stylesheet stylesheet){
		ArrayList<ASTNode> children = stylesheet.getChildren();
		for (ASTNode child : children) {
			addScopeSpace();
			if (child instanceof Stylerule) {
				generateStylerule((Stylerule) child);
			}
		}
	}

	private void generateStylerule(Stylerule stylerule) {
		for (Selector selector : stylerule.selectors) {
			stringBuilder.append(selector);
			addSpace();
		}
		stringBuilder.append(" {\n");
		increaseScope();
		for (ASTNode node : stylerule.body) {
			addScopeSpace();
			if(node instanceof Declaration) {
				generateDeclaration((Declaration) node);
			}
		}
		decreaseScope();
		stringBuilder.append("}");

		lineSkip();
	}

	private void generateDeclaration(Declaration declaration) {
		stringBuilder.append(declaration.property.name);
		stringBuilder.append(": ");
		if (declaration.expression instanceof BoolLiteral) {
			BoolLiteral literal = (BoolLiteral) declaration.expression;
			stringBuilder.append(literal.value);
		}
		if (declaration.expression instanceof ColorLiteral) {
			ColorLiteral literal = (ColorLiteral) declaration.expression;
			stringBuilder.append(literal.value);
		}
		if (declaration.expression instanceof PercentageLiteral) {
			PercentageLiteral literal = (PercentageLiteral) declaration.expression;
			stringBuilder.append(literal.value);
		}
		if (declaration.expression instanceof PixelLiteral) {
			PixelLiteral literal = (PixelLiteral) declaration.expression;
			stringBuilder.append(literal.value);
		}
		if (declaration.expression instanceof ScalarLiteral) {
			ScalarLiteral literal = (ScalarLiteral) declaration.expression;
			stringBuilder.append(literal.value);
		}
		stringBuilder.append(";");
		lineSkip();
	}

	private void lineSkip(){
		stringBuilder.append("\n");
	}

	private void addSpace() {
		stringBuilder.append(" ");
	}

	private void addScopeSpace() {
		for (int i = 0; i < scope; i++) {
			stringBuilder.append("	");
		}
	}

	private void increaseScope(){
		scope++;
	}
	private void decreaseScope(){
		scope--;
	}
}
