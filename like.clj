(ns like)

(defn like
  "Checks if given obj is structured the same way as template, 
meaning that, for all elements of template, the elements of obj must have the same class. Also checks nested.
For example, [6 2] will be like [1], and [{:a 1}] will be like [{:a 3}], but not like [{1 3}].
[#{:a :b}] will be like [#{:c :d}], and like [], but not like [3].
For collections, the similarity is not determined by class, but instead by clojure's collection categories,
meaning that e.g. a KeyValuePair will be similar to a PersistentVector because both satisfy the 'vector?' predicate.
If you want the class to be exactly matched, add {:strict true} to the collection metadata.
The same will apply for map?, seq? and set?, and only for other cases the exact class will be compared by default.
For maps where all keys are keywords, the similarity is more strict: each key in template must be present in obj, 
and the correspondent values must be alike.
Nil will not be likened to anything but nil.
'Like' will only check obj as long as there are more items in template. Which is good for performance,
but also makes this function rather unsuited for heterogeneous collections. 
Heterogenous maps are also most likely not to be checked correctly, since the order is arbitrary."
  [template obj]
  (and
    (if (-> template meta :strict)
      (= (class template) (class obj))
      (or 
        (and (map? template) (map? obj))
        (and (seq? template) (seq? obj))
        (and (vector? template) (vector? obj))
        (and (set? template) (set? obj))
        (= (class template) (class obj))))
    (cond
      (and (map? template) (every? keyword? (keys template)))
      (and (map? obj) (->> (map like (vals template) (map obj (keys template)))
                        (every? true?)))
      (coll? template)
      (and (coll? obj) (->> (map like template obj)
                         (every? true?)))
      :else true)))

(comment 

;This is how it will look in the code. Note that in production, these checks would run every time.
;For the sake of performance, you should make macros to yield pre/postconditions that only check e.g. in test mode.

(defn calendar
  [c start-date end-date]
  {:pre [(like {:appointments {} :new (list)} c)]
   :post [(like {(date 2012 3 1) {:occupations [] :appointments #{}}} %)]}
   ;huge pile of code
  ...)

(defn german-holidays
  "Returns map of german holidays, in the year of d, or between from and to. 
Key is the date, and value the keyword of the holiday."
  [d]
    {:post [(like {(date 2012 1 1) :easter} %)]}
    (let [y (t/year d)
          e (easter d)]
      {(date y 1 1) :new-year
       (date y 2 14) :valentines
       (date y 5 1) :labour-day
       (date y 10 3) :unification
       (date y 11 1) :all-saints
       (date y 12 24) :christmas-eve
       (date y 12 25) :christmas
       (date y 12 26) :christmas-monday
       (date y 12 31) :sylvester
       (add-day -48 e) :rose-monday
       (add-day -7 e) :palm-sunday
       (add-day -2 e) :good-friday
       e :easter
       (add-day 1 e) :easter-monday
       (add-day 39 e) :ascension
        ...
       (add-day 60 e) :corpus-christi }))

)
