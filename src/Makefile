ifndef VERBOSE
.SILENT:
endif

RESOURCES := $(shell find . | grep .*\\.java$)

all: pushfight

pushfight: clean $(RESOURCES)
	javac $(RESOURCES)

text gui evolve evaluate debug: pushfight
	java -Xmx5g main/java/PushFight $@

clean:
	$(shell find . -name "*.class" -type f -delete)