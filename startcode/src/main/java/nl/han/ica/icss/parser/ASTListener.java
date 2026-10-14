package nl.han.ica.icss.parser;

import java.util.ArrayList;
import java.util.Stack;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import nl.han.ica.icss.parser.ICSSBaseListener;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(nl.han.ica.icss.parser.ICSSParser.StylesheetContext ctx) {
		ASTNode stylesheet = new Stylesheet();
		currentContainer.push(stylesheet);
	}

	@Override
	public void exitStylesheet(nl.han.ica.icss.parser.ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = (Stylesheet) currentContainer.pop();
		ast.setRoot(stylesheet);
	}


	@Override
	public void enterStylerule(nl.han.ica.icss.parser.ICSSParser.StyleruleContext ctx) {
		ASTNode stylerule = new Stylerule();
		currentContainer.push(stylerule);
	}

	@Override
	public void exitStylerule(nl.han.ica.icss.parser.ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = (Stylerule) currentContainer.pop();
		currentContainer.peek().addChild(stylerule);
	}


	@Override
	public void enterTagselector(nl.han.ica.icss.parser.ICSSParser.TagselectorContext ctx) {
		ASTNode tagSelector = new TagSelector(ctx.getText());
		currentContainer.push(tagSelector);
	}

	@Override
	public void exitTagselector(nl.han.ica.icss.parser.ICSSParser.TagselectorContext ctx) {
		TagSelector tagSelector = (TagSelector) currentContainer.pop();
		currentContainer.peek().addChild(tagSelector);
	}


	@Override
	public void enterClassselector(nl.han.ica.icss.parser.ICSSParser.ClassselectorContext ctx) {
		ASTNode classselector = new ClassSelector(ctx.getText());
		currentContainer.push(classselector);
	}

	@Override
	public void exitClassselector(nl.han.ica.icss.parser.ICSSParser.ClassselectorContext ctx) {
		ClassSelector classSelector = (ClassSelector) currentContainer.pop();
		currentContainer.peek().addChild(classSelector);
	}


	@Override
	public void enterIdselector(nl.han.ica.icss.parser.ICSSParser.IdselectorContext ctx) {
		ASTNode idSelector = new IdSelector(ctx.getText());
		currentContainer.push(idSelector);
	}

	@Override
	public void exitIdselector(nl.han.ica.icss.parser.ICSSParser.IdselectorContext ctx) {
		IdSelector idSelector = (IdSelector) currentContainer.pop();
		currentContainer.peek().addChild(idSelector);
	}


	@Override
	public void enterDeclaration(nl.han.ica.icss.parser.ICSSParser.DeclarationContext ctx) {
		ASTNode declaration = new Declaration();
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(nl.han.ica.icss.parser.ICSSParser.DeclarationContext ctx) {
		Declaration declaration = (Declaration) currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	@Override
	public void enterPropertyname(nl.han.ica.icss.parser.ICSSParser.PropertynameContext ctx) {
		ASTNode propertyname = new PropertyName(ctx.getText());
		currentContainer.push(propertyname);
	}

	@Override
	public void exitPropertyname(nl.han.ica.icss.parser.ICSSParser.PropertynameContext ctx) {
		PropertyName propertyName = (PropertyName) currentContainer.pop();
		currentContainer.peek().addChild(propertyName);
	}


	@Override
	public void enterBooleanvalue(nl.han.ica.icss.parser.ICSSParser.BooleanvalueContext ctx) {
		ASTNode booleanValue = new BoolLiteral(ctx.getText());
		currentContainer.push(booleanValue);
	}

	@Override
	public void exitBooleanvalue(nl.han.ica.icss.parser.ICSSParser.BooleanvalueContext ctx) {
		BoolLiteral boolLiteral = (BoolLiteral) currentContainer.pop();
		currentContainer.peek().addChild(boolLiteral);
	}


	@Override
	public void enterColorvalue(nl.han.ica.icss.parser.ICSSParser.ColorvalueContext ctx) {
		ASTNode colorvalue = new ColorLiteral(ctx.getText());
		currentContainer.push(colorvalue);
	}

	@Override
	public void exitColorvalue(nl.han.ica.icss.parser.ICSSParser.ColorvalueContext ctx) {
		ColorLiteral colorLiteral = (ColorLiteral) currentContainer.pop();
		currentContainer.peek().addChild(colorLiteral);
	}


	@Override
	public void enterPercentagevalue(nl.han.ica.icss.parser.ICSSParser.PercentagevalueContext ctx) {
		ASTNode percentagevalue = new PercentageLiteral(ctx.getText());
		currentContainer.push(percentagevalue);
	}

	@Override
	public void exitPercentagevalue(nl.han.ica.icss.parser.ICSSParser.PercentagevalueContext ctx) {
		PercentageLiteral percentageLiteral = (PercentageLiteral) currentContainer.pop();
		currentContainer.peek().addChild(percentageLiteral);
	}


	@Override
	public void enterPixelvalue(nl.han.ica.icss.parser.ICSSParser.PixelvalueContext ctx) {
		ASTNode pixelValue = new PixelLiteral(ctx.getText());
		currentContainer.push(pixelValue);
	}

	@Override
	public void exitPixelvalue(nl.han.ica.icss.parser.ICSSParser.PixelvalueContext ctx) {
		PixelLiteral pixelvalue = (PixelLiteral) currentContainer.pop();
		currentContainer.peek().addChild(pixelvalue);
	}


	@Override
	public void enterScalarvalue(nl.han.ica.icss.parser.ICSSParser.ScalarvalueContext ctx) {
		ASTNode scalarValue = new ScalarLiteral(ctx.getText());
		currentContainer.push(scalarValue);
	}

	@Override
	public void exitScalarvalue(nl.han.ica.icss.parser.ICSSParser.ScalarvalueContext ctx) {
		ScalarLiteral scalarValue = (ScalarLiteral) currentContainer.pop();
		currentContainer.peek().addChild(scalarValue);
	}


	@Override
	public void enterVariableassignment(nl.han.ica.icss.parser.ICSSParser.VariableassignmentContext ctx) {
		ASTNode variableAssignment = new VariableAssignment();
		currentContainer.push(variableAssignment);
	}

	@Override
	public void exitVariableassignment(nl.han.ica.icss.parser.ICSSParser.VariableassignmentContext ctx) {
		VariableAssignment variableAssignment = (VariableAssignment) currentContainer.pop();
		currentContainer.peek().addChild(variableAssignment);
	}


	@Override
	public void enterVariablereference(nl.han.ica.icss.parser.ICSSParser.VariablereferenceContext ctx) {
		ASTNode variableReference = new VariableReference(ctx.getText());
		currentContainer.push(variableReference);
	}

	@Override
	public void exitVariablereference(nl.han.ica.icss.parser.ICSSParser.VariablereferenceContext ctx) {
		VariableReference variableReference = (VariableReference) currentContainer.pop();
		currentContainer.peek().addChild(variableReference);
	}


	@Override
	public void enterAddoperation(nl.han.ica.icss.parser.ICSSParser.AddoperationContext ctx) {
		ASTNode addOperation = new AddOperation();
		currentContainer.push(addOperation);
	}

	@Override
	public void exitAddoperation(nl.han.ica.icss.parser.ICSSParser.AddoperationContext ctx) {
		AddOperation addOperation = (AddOperation) currentContainer.pop();
		currentContainer.peek().addChild(addOperation);
	}


	@Override
	public void enterSubtractoperation(nl.han.ica.icss.parser.ICSSParser.SubtractoperationContext ctx) {
		ASTNode subtractOperation = new SubtractOperation();
		currentContainer.push(subtractOperation);
	}

	@Override
	public void exitSubtractoperation(nl.han.ica.icss.parser.ICSSParser.SubtractoperationContext ctx) {
		SubtractOperation subtractOperation = (SubtractOperation) currentContainer.pop();
		currentContainer.peek().addChild(subtractOperation);
	}


	@Override
	public void enterMultiplyoperation(nl.han.ica.icss.parser.ICSSParser.MultiplyoperationContext ctx) {
		ASTNode multiplyOperation = new SubtractOperation();
		currentContainer.push(multiplyOperation);
	}

	@Override
	public void exitMultiplyoperation(nl.han.ica.icss.parser.ICSSParser.MultiplyoperationContext ctx) {
		MultiplyOperation multiplyOperation = (MultiplyOperation) currentContainer.pop();
		currentContainer.peek().addChild(multiplyOperation);
	}


	@Override
	public void enterIfclause(nl.han.ica.icss.parser.ICSSParser.IfclauseContext ctx) {
		ASTNode ifClause = new IfClause();
		currentContainer.push(ifClause);
	}

	@Override
	public void exitIfclause(nl.han.ica.icss.parser.ICSSParser.IfclauseContext ctx) {
		IfClause ifClause = (IfClause) currentContainer.pop();
		currentContainer.peek().addChild(ifClause);
	}


	@Override
	public void enterElseclause(nl.han.ica.icss.parser.ICSSParser.ElseclauseContext ctx) {
		ASTNode elseClause = new ElseClause();
		currentContainer.push(elseClause);
	}

	@Override
	public void exitElseclause(nl.han.ica.icss.parser.ICSSParser.ElseclauseContext ctx) {
		ElseClause elseClause = (ElseClause) currentContainer.pop();
		currentContainer.peek().addChild(elseClause);
	}
}