#!/usr/bin/env python

import sys
import os
import subprocess
import random

try:
	import cStringIO as StringIO
except ImportError:
	import StringIO

ROOT_CLASS = "chaldea.Main"
JAVAC_ARGS = ["-Xlint:deprecation"]
JAVA_ARGS = []

class SystemException(Exception):
	def __init__(self, code):
		self.code = code

def _root(func, *vargs, **dargs):
	root_directory = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
	os.chdir(root_directory)
	
	try:
		return_value = func(*vargs, **dargs)
	finally:
		os.chdir(root_directory)
	
	return return_value

def _shell(*cmd, **options):
	out = None
	
	if options.get("quiet", False) == True:
		out = subprocess.PIPE
	
	exit = subprocess.call(cmd, stdout=out, stderr=out)
	
	if exit != 0:
		raise SystemException(exit)

def _requisite(directory):
	if not os.path.isdir(directory):
		os.mkdir(directory)

def _exists(directory):
	return os.path.isdir(director)

def _cd(directory):
	os.chdir(directory)

def _run(main_class=ROOT_CLASS):
	_shell("java", "-cp", "build:lib/magarathea.jar", main_class)

def _class_path():
	return "%s:." % ':'.join(_scan(os.path.join("..", "lib"), ".jar"))
	
def _scan(directory, extension):
	output = [ ]
	
	for root, dirs, files in os.walk(directory):
		for filename in files:
			if filename.endswith(extension):
				output.append(os.path.join(root, filename))
	
	return output
	
def _build(package, classes=None):
	_requisite("build")
	_cd("src")
	
	if classes == None:
		classes = _scan(package.replace(".", os.path.sep), ".java")
	
	output_directory = os.path.join("..", "build")
	
	args = [ ]
	args += JAVAC_ARGS
	args += ["-classpath", _class_path()]
	args += ["-d", output_directory]
	args += classes
	
	_shell("javac", *args)

def do_grammar():
	_cd("src")
	
	args = [ "apt", "-nocompile", "-s", ".", "-classpath", _class_path() ]
	args += [ "-Ahelp", "-Arecoverty=true", "-Ayyparseerror=true" ] 
	args += [ "chaldea/parser/ChaldeaParser.java" ]
	
	_shell(*args, quiet=False)

def do_compile():
	_build("chaldea")

def _do_doc(*arguments):
	_requisite("doc")
	_cd("src")
	
	main_package = ".".join(ROOT_CLASS.split(".")[:-1])
	doc_root = os.path.join("..", "doc")
	
	arguments = list(arguments)
	arguments.append("-classpath")
	arguments.append(_class_path())
	arguments.extend(["-d", doc_root, main_package])
	
	_shell("javadoc", *arguments)

def do_test():
	_run(ROOT_CLASS)

def _main(arguments):
	script = [ ]
	should_clear = False
	
	i = 0
	while i < len(arguments):
		argument = arguments[i]
		
		if argument[:1] == '+':
			if i + 1 == len(arguments):
				j = i + 1
			else:
				for j in xrange(i + 1, len(arguments)):
					if arguments[j][:1] == '+':
						break
				else:
					j += 1
			
			
			method_arguments = arguments[i+1:j]
			method_name = "do_%s" % argument[1:]

			i = j

			if argument == "+clear":
				should_clear = True
				
				continue
			else:
				if globals().get(method_name, None):
					method = globals()[method_name]
				
					if callable(method):
						script.append((argument, method, method_arguments))
					
						continue
			
			sys.stderr.write("""\
Build error: \033[1mUnknown action \"%s\"\033[0m.

""" % argument)
			return 1
		
		else:
			sys.stderr.write("""\
Build error: \033[1mInvalid argument \"%s\" (expecting an +action.)\033[0m

""" % argument)
			return 2
	
	print "===> \033[1;33mDon't panic!\033[0;33m build initializing..\033[0m"
	
	for name, method, arguments in script:
		print "=> Begin stage %s\n" % ("\033[31m%s\033[0m" % name)
		
		try:
			_root(method, *arguments)
		except SystemException, e:
			print "=> System exit %s" % e.code
			break
		print
	else:
		print "===> \033[33mBuild complete.\033[0m"
if __name__ == "__main__":
	sys.exit(_main(sys.argv[1:]))