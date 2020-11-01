ifndef VERBOSE
.SILENT:
endif

RESOURCES := $(shell find . | grep .*\\.java$)

all: pushfight

pushfight: $(RESOURCES)
	javac $(RESOURCES)

text gui evolve evaluate debug: pushfight
	java main/java/PushFight $@