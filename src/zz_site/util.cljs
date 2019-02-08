(ns zz-site.util)


(defn need-buttons-fn
  "returns vector of numbers with mask [-2 -1 0 +1 +2 +3] for :nth where all numbers in (range 1 :pages)
  (f 2 4)   ->  [1 2 3]
  (f 2 10)  ->  [1 2 3 4 5 6]
  (f 3 4)   ->  [1 2 3]
  (f 6 7)   ->  [2 3 4 5 6]"
  [nth pages]
  (let [stand     [-2 -1 0 +1 +2 +3]
        p-max     (dec pages)
        mapped    (mapv (partial + nth) stand)
        ltrim     (when-not (pos-int? (first mapped))
                    (take p-max (mapv (partial + (- 1 (first mapped))) mapped)))
        rtrim     (when (< p-max (last mapped))
                    (take-last p-max (mapv #(- % (- (last mapped) p-max)) mapped)))]
    (concat (or ltrim rtrim mapped) [pages])))



(defn ^:export toggle-sidebar []
  (if (js/document.querySelector "body.sidebar-visible")
    (.removeAttribute js/document.body "class")
    (.setAttribute js/document.body "class" "sidebar-visible")))


(let [idx 4 size 8
      stand  [-2 -1 0 1 2 3]
      mapped (mapv (partial + idx) stand)
      ltrim (when (neg? (first mapped))
              (take size (map #(- % (first mapped)) mapped)))
      rtrim (when (<  (dec size) (last mapped))
              (take-last size (map #(- % (- (last mapped) (dec size))) mapped)))]
  (or ltrim rtrim mapped))
