# Goal

Implement a high-performance IndexedSeq and ensure its performance is comparable
to both Vector and List.

# Definitions

Let $Op(Seq)$ denote the Set of operations on Sequence $Seq$.
Examples are prepend, head, get, concat, ...
Let $time(op), op\in Op(Seq)$ denote the time (in seconds) required to execute operation $op$.

Let $Op_{fast}(Seq) \subset Op(Seq)$ be the subset of *fast* operations,
generally they run in $O(1)$ or $O(\log n)$.
Let $Op_{slow}(Seq) = Op(Seq) \setminus Op_{fast}(Seq)$ be the subset of *slow*
operations, they mostly run in $O(n)$.

Examples:
* $head, tail, prepend, get(2), \ldots \in Op_{fast}(List)$
* $last, get, concat, \ldots \in Op_{slow}(List)$
* $get, head, tail, \ldots \in Op_{fast}(Vector)$
* $concat \in Op_{slow}(Vector)$


