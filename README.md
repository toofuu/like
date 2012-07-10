like
====

A pragmatic approach to type information in Clojure

Dynamic typing is nice, but sometimes I would like to have more type information when the data is complex, or I have not looked at the function for a while.
Wouldn't it be nice to have *verifiable* documention of what data a function needs, and how the result of the function looks like?

This small little function makes code easier to reason about for me. It's called *like*.
It checks the *shape* of things. 

```clojure
(like [] [1 :keyword '()]) => true

(like [1] [1 2 3]) => true

(like [:keyword] [1 2 3]) => false

(like [{:name "" :address ""}] [{:name "me" :address "here"} {:name "you" :address "there"}]) => true

(like {:name "someone" :address "somewhere"} {:name "me"}) => false

(like #{1} [1]) => false
```

You can use it as pre- or postcondition, so the similarity of in/output is succinctly documented and checked automatically.
And you do not have to use it everywhere, only where you feel it helps you understand the code better.

```clojure (fn parse-int [str] ...)``` needs no type or unit-test to be understood.

```clojure (fn parse-appointment [str] ...)``` is better understood when it has ```clojure {:post [(like {:id (UUID.) :name "me" :date (java.util.Date .)} %)]}```
