from sklearn.neural_network import MLPClassifier
import csv
import numpy as np
from sklearn.cluster import KMeans
from sklearn import tree
from sklearn.metrics import accuracy_score
from sklearn.ensemble import RandomForestClassifier
from sklearn.datasets import make_classification
from sklearn import svm
import matplotlib
import matplotlib.pyplot as plt
from sklearn.manifold import TSNE
from sklearn.model_selection import KFold
from scipy.stats import mode
from sklearn.utils import shuffle
from sklearn.decomposition import PCA
from sklearn.metrics import precision_recall_curve
from sklearn.feature_selection import SelectKBest, chi2
import scikitplot as skplt
from sklearn.metrics import precision_score, recall_score

arr = []
arr_complexities = []
arr_names = []
complexity_dictionary = {'n':0, 'n_square':1, 'logn':2, 'nlogn':3, '1':4}
color_mapping = {0:'r', 1:'g', 2:'b', 3:'y', 4:'m'}
file_to_complexity_mapping = {}
important_features = [0,2,3,5,6,7,8,9]
rejected_names = []

with open('./finalFeatureData.csv','rt') as f:
	data = csv.reader(f)
	count = 0
	for row in data:
		count = count + 1
		if(count==1):
			continue
		name = row[-1]
		features = row[0:-2]
		complexity = row[-2]
		if(complexity==('nlogn' or 'logn')):
			continue
		for i in features:
			i = (int)(i)
		arr_names.append(name)
		arr.append(features)
		arr_complexities.append(complexity_dictionary[complexity])

arr = np.asarray(arr, dtype=float)
arr_complexities = np.asarray(arr_complexities)

# shuffle the data
arr, arr_complexities, arr_names = shuffle(arr, arr_complexities, arr_names, random_state=0)

accuracy_arr = []
recall_arr = []
precision_arr = []

n_classes = 5
no_of_variables = 14
scores = []


for i in range(1, no_of_variables):
	array = arr[:, :i]
	kf = KFold(n_splits=5, shuffle=False) 
	count = 0
	score = []
	prec = []
	rec = []
	
	for train_index, test_index in kf.split(array, arr_complexities):
		count += 1
		X_train, X_test = array[train_index], array[test_index]
		y_train, y_test = arr_complexities[train_index], arr_complexities[test_index]
		clf = MLPClassifier(solver='lbfgs', alpha=1e-5,hidden_layer_sizes=(5, 2), random_state=1)
		clf.fit(X_train, y_train)
		y_predicted = clf.predict(X_test)
		acc_score = accuracy_score(y_test, y_predicted)
		score.append(acc_score)
		prec.append(precision_score(y_test, y_predicted, average='weighted'))
		rec.append(recall_score(y_test, y_predicted, average='weighted'))
	scores.append(max(score))
	precision_arr.append(max(prec))
	recall_arr.append(max(rec))

print('Scores: ', scores)
plt.plot(scores, "s-")
plt.savefig('./mlp_vs_variable_count.png')
