# -------------------------------------------------------------------------- #
# Makefile for improved Yahtzee game.                                        #
# Author:  Taylor Durrer                                                     #
# Date:    August 11, 2015                                                   #
# -------------------------------------------------------------------------- #

# Java compiler
JCC = javac

# Display debugging information when compiling
JFLAGS = -g

# Source files to be compiled
J_SRCS = Yahtzee.java ScoreController.java Score.java ActiveScore.java\
         AutoScore.java DiceController.java Dice.java

default:
	$(JCC) $(JFLAGS) $(J_SRCS)
	echo 'java Yahtzee $$*' > Yahtzee
	chmod ug+rx Yahtzee

clean:
	rm -f *.class Yahtzee

new:
	make clean
	make

