## Helple

![Helple icon](https://raw.githubusercontent.com/mrugacz95/Helple/main/images/helple_app_icon.svg)

This appliction will help you to solve polish Wordle game.

It's written in Jetpack Compose + ViewModels + Hilt + Guava + Coroutines

There are three algorithms which can be used to find solution.
1. Simple: Just eliminating words which will not be answer,
2. Minimax: Minimax algorith which tries to find word with shortest possible answers,
3. Entropy: Chooses word which will gain most information, based on mathematical entropy.

You can switch between 5 and 6 letters long. Try to find secret ord which will make app use more than 6 guesses.

##### Screenshots

![Screenshot](https://user-images.githubusercontent.com/12548284/174679050-4b36b3a6-8f5b-4662-84d9-0b5156a3b0c1.gif)

##### Compiled apk

You can find build apk in release tab. Soon I will also publish it on Google Play.

##### Contribution

If you want to add some improvement, here are few ideas:

* add "about" section
* Improve existing algorithms/add new solver
* Implement logic to adjust number of checked permutations to number of words left
* Allow to change suggested word for another
* Let user enter own word, instead of using suggestions
* Display more suggestions aat once


#### Other languages

It's possible to adjust application to other language, just replace database under path: Helple/app/src/main/assets/database/words.db

```sql
BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "words" (
	"id"	BIGINT NOT NULL,
	"letter0"	VARCHAR NOT NULL,
	"letter1"	VARCHAR NOT NULL,
	"letter2"	VARCHAR NOT NULL,
	"letter3"	VARCHAR NOT NULL,
	"letter4"	VARCHAR NOT NULL,
	"letter5"	VARCHAR NOT NULL,
	"length"	INTEGER NOT NULL,
	PRIMARY KEY("id")
);
CREATE INDEX IF NOT EXISTS "ix_words_length_letter" ON "words" (
	"length",
	"letter0",
	"letter1",
	"letter2",
	"letter3",
	"letter4",
	"letter5"
);
COMMIT;
```
