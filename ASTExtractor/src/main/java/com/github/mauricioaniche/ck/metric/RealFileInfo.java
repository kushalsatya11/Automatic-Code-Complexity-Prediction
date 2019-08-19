package com.github.mauricioaniche.ck.metric;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import javafx.util.Pair;

public class RealFileInfo extends ASTVisitor {
	
	ArrayList<Pair<String,String>> methodReachableFromMain;
	
	public int noOfIfs = 0;
	public int noOfSwitches = 0;
	public int noOfLoops = 0;
	public int noOfBreaks = 0;
	public int noOfPriorityQueue = 0;
	public int noOfSort = 0;
	public int noOfHashSet = 0;
	public int noOfHashMap = 0;
	public int noOfRecursion = 0;
	
	private Pair<String,String> currentMethod;
	
	public RealFileInfo(ArrayList<Pair<String,String>> methodReachableFromMain) {
		 this.methodReachableFromMain = methodReachableFromMain;
	 }
	
	@Override 
	public boolean visit(MethodDeclaration node) {
		String methodName = FileInfo.getFullMethodName(node.resolveBinding());
		String className = FileInfo.getFullClassName(node.resolveBinding());
		if(methodName.equals("main"))
		{
			currentMethod = new Pair<String,String>(methodName, className);
			return true;
		}
		for(Pair<String,String> methodClassPair : methodReachableFromMain)
		{
			if(methodClassPair.getKey().equals(methodName) && methodClassPair.getValue().equals(className))
			{
				currentMethod = new Pair<String,String>(methodName, className);
				return true;
			}
		}
		currentMethod = null;
		return false;
	}
	
	@Override
	public void endVisit(MethodDeclaration node) {
		currentMethod = null;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		String methodName = FileInfo.getFullMethodName(node.resolveMethodBinding());
		String className = FileInfo.getFullClassName(node.resolveMethodBinding());
		if(methodName != null && className != null)
		{
			if(currentMethod.getKey().equals(methodName) && currentMethod.getValue().equals(className))
				noOfRecursion = 1;
			if(methodName.equals("sort") && (className.equals("java.util.Arrays") || className.equals("java.util.Collections")))
					noOfSort += 1;
			if(className.equals("java.util.HashMap"))
				noOfHashSet = 1; //verify
			if(className.equals("java.util.HashSet"))
				noOfHashMap = 1; //verify
			if(className.equals("java.util.PriorityQueue"))
				noOfPriorityQueue = 1;
		}
		return false;
	}
	
	@Override
	public boolean visit(ConstructorInvocation node) { //
		String methodName = FileInfo.getFullMethodName(node.resolveConstructorBinding());
		String className = FileInfo.getFullClassName(node.resolveConstructorBinding());
		if(methodName != null && className != null)
		{
			if(currentMethod.getKey().equals(methodName) && currentMethod.getValue().equals(className))
				noOfRecursion = 1;
			if(className.equals("java.util.HashMap"))
				noOfHashSet = 1; //verify
			if(className.equals("java.util.HashSet"))
				noOfHashMap = 1; //verify
			if(className.equals("java.util.PriorityQueue"))
				noOfPriorityQueue = 1;
		}
		
		return false;		
	}
	
	@Override
	public boolean visit(IfStatement node) {
		noOfIfs += 1;
		return true;
	}
	
    private int current = 0;
    public int noOfNestedLoops = 0;

    @Override
    public boolean visit(ForStatement node) {
        current++;
        noOfNestedLoops = Math.max(current, noOfNestedLoops);
        noOfLoops += 1;

        return super.visit(node);
    }

    @Override
    public void endVisit(ForStatement node) {
        current--;
    }
    
    @Override
    public void endVisit(EnhancedForStatement node) {
        current--;
    }
    
    @Override
    public void endVisit(DoStatement node) {
        current--;
    }
    
    @Override
    public void endVisit(WhileStatement node) {
        current--;
    }
    
    @Override
    public boolean visit(EnhancedForStatement node) {
        noOfLoops += 1;
        current++;
        noOfNestedLoops = Math.max(current, noOfNestedLoops);

        return super.visit(node);
    }

    @Override
    public boolean visit(DoStatement node) {
        noOfLoops += 1;
        current++;
        noOfNestedLoops = Math.max(current, noOfNestedLoops);
        
        return super.visit(node);
    }

    @Override
    public boolean visit(WhileStatement node) {
        noOfLoops += 1;

        current++;
        noOfNestedLoops = Math.max(current, noOfNestedLoops);
        
        return super.visit(node);
    }
    
    @Override
    public boolean visit(SwitchStatement node) {
    		noOfSwitches += 1;
    		return true;
    }
    
    @Override
    public boolean visit(BreakStatement node) {
    		noOfBreaks +=  1;
    		return true;
    }
	
}
