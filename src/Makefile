ifndef VERBOSE
.SILENT:
endif

RESOURCES := $(shell find . | grep .*\\.java$)
all: pushfight

pushfight: $(RESOURCES)
	javac $(RESOURCES)

text: pushfight
	java main/java/PushFight text

gui: pushfight
	java main/java/PushFight gui

test: pushfight
	java main/java/PushFight