package com.github.mauricioaniche.ck;

import static com.github.mauricioaniche.ck.util.LOCCalculator.calculate;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import com.github.mauricioaniche.ck.metric.ClassInfo;
import com.github.mauricioaniche.ck.metric.ClassLevelMetric;
import com.github.mauricioaniche.ck.metric.FileInfo;
import com.github.mauricioaniche.ck.metric.IgnoreSubClasses;
import com.github.mauricioaniche.ck.metric.MethodLevelMetric;
import com.github.mauricioaniche.ck.metric.MethodLevelVisitor;
import com.github.mauricioaniche.ck.metric.NumberOfLoops;
import com.github.mauricioaniche.ck.metric.RealFileInfo;

import javafx.util.Pair;


public class MetricsExecutor extends FileASTRequestor {

	private Callable<List<ClassLevelMetric>> classLevelMetrics;
	private Callable<List<MethodLevelMetric>> methodLevelMetrics;
	private CKNotifier notifier;
	
	private static final String[] CLASS_HEADER = {"fileName","noOfIfs", "noOfSwitches", "noOfLoops", "noOfBreaks","noOfPriorityQueue", "noOfSort", "HashSetPresent", "HashMapPresent","RecursionPresent", "noOfNestedLoops"};
	
    private CSVPrinter classPrinter;
    
    private String outputFile = "extractedData.csv";

	private static Logger log = Logger.getLogger(MetricsExecutor.class);
	
	public MetricsExecutor(Callable<List<ClassLevelMetric>> classLevelMetrics, Callable<List<MethodLevelMetric>> methodLevelMetrics, CKNotifier notifier) {
		this.classLevelMetrics = classLevelMetrics;
		this.methodLevelMetrics = methodLevelMetrics;
		this.notifier = notifier;
        FileWriter classOut;
		try {
			classOut = new FileWriter(outputFile);
	        this.classPrinter = new CSVPrinter(classOut, CSVFormat.DEFAULT.withHeader(CLASS_HEADER));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@Override
	public void acceptAST(String sourceFilePath, 
			CompilationUnit cu) {
		
		CKClassResult result = null;
		
		try {
			FileInfo fileInfo = new FileInfo();
			ClassInfo info = new ClassInfo();
			
			cu.accept(fileInfo);
			fileInfo.updateMethodReachableFromMain();
			ArrayList<Pair<String,String>> methodReachableFromMain = fileInfo.methodReachableFromMain;
			
//			NumberOfLoops numberOfLoops = new NumberOfLoops(methodReachableFromMain);
//			cu.accept(numberOfLoops);
//			
//			int x = numberOfLoops.qty;
//			
//			log.info(x);
//			
			RealFileInfo realFileInfo = new RealFileInfo(methodReachableFromMain);
			cu.accept(realFileInfo);
			
			
			
			int noOfIfs = realFileInfo.noOfIfs;
			int noOfSwitches =realFileInfo.noOfSwitches;
			int noOfLoops = realFileInfo.noOfLoops;
			int noOfBreaks = realFileInfo.noOfBreaks;
			int noOfPriorityQueue = realFileInfo.noOfPriorityQueue;
			int noOfSort = realFileInfo.noOfSort;
			int noOfHashSet = realFileInfo.noOfHashSet;
			int noOfHashMap = realFileInfo.noOfHashMap;
			int noOfRecursion = realFileInfo.noOfRecursion;
			int noOfNestedLoops = realFileInfo.noOfNestedLoops;
			
			log.info(noOfNestedLoops);
			
			this.classPrinter.printRecord(sourceFilePath, noOfIfs, noOfSwitches, noOfLoops, noOfBreaks, noOfPriorityQueue, noOfSort, noOfHashSet, noOfHashMap, noOfRecursion, noOfNestedLoops);
			
//			cu.accept(info);
//			if(info.getClassName()==null) return;
//		
//			result = new CKClassResult(sourceFilePath, info.getClassName(), info.getType());
//			
//			int loc = calculate(new FileInputStream(sourceFilePath));
//			result.setLoc(loc);
//
//			// calculate class level classLevelMetrics
//			for(ClassLevelMetric visitor : classLevelMetrics.call()) {
//				visitor.execute(cu, result);
//				visitor.setResult(result);
//			}
//
//			// calculate metric level classLevelMetrics
//			MethodLevelVisitor methodLevelVisitor = new MethodLevelVisitor(methodLevelMetrics, cu);
//			ASTVisitor astVisitor = new IgnoreSubClasses(methodLevelVisitor);
//			cu.accept(astVisitor);
//			result.setMethods(methodLevelVisitor.getMap());
//
//			log.info(result);
//			notifier.notify(result);
		} catch(Exception e) {
			log.error("error in " + sourceFilePath, e);
		}
	}
	
	public void destroyFileWriter() {
        try {
			this.classPrinter.flush();
	        this.classPrinter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
