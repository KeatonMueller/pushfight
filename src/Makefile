# ifndef VERBOSE
# .SILENT:
# endif

RESOURCES := $(shell find . | grep .*\\.java$)
all: pushfight text

pushfight: $(RESOURCES)
	javac $(RESOURCES)

text: pushfight
	java PushFight
