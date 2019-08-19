package com.github.mauricioaniche.ck.metric;

import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.CKMethodResult;
import javafx.util.Pair;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.*;

public class NumberOfLoops extends ASTVisitor implements ClassLevelMetric, MethodLevelMetric {

	public int qty = 0;
	ArrayList<Pair<String,String>> methodReachableFromMain;
	
	 public NumberOfLoops(ArrayList<Pair<String,String>> methodReachableFromMain) {
		 this.methodReachableFromMain = methodReachableFromMain;
	 }

	public boolean visit(EnhancedForStatement node) {
		qty++;
		return super.visit(node);
	}
	
	@Override 
	public boolean visit(MethodDeclaration node) {
		String methodName = FileInfo.getFullMethodName(node.resolveBinding());
		String className = FileInfo.getFullClassName(node.resolveBinding());
		if(methodName.equals("main"))
			return true;
		for(Pair<String,String> methodClassPair : methodReachableFromMain)
		{
			if(methodClassPair.getKey().equals(methodName) && methodClassPair.getValue().equals(className))
				return true;
		}
		
		return false;
	}

	public boolean visit(DoStatement node) {
		qty++;
		return super.visit(node);
	}

	public boolean visit(WhileStatement node) {
		qty++;
		return super.visit(node);
	}

	public boolean visit(ForStatement node) {
		qty++;
		return super.visit(node);
	}

	@Override
	public void setResult(CKMethodResult result) {
		result.setLoopQty(qty);

	}

	@Override
	public void execute(CompilationUnit cu, CKClassResult number) {
		cu.accept(new IgnoreSubClasses(this));
	}

	@Override
	public void setResult(CKClassResult result) {
		result.setLoopQty(qty);
	}
}
