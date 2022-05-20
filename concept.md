# Goal

Implement a high-performance IndexedSeq and ensure its performance is comparable
to both Vector and List.

# Definitions

Let $Seq\in\{List, Vector, Elwms\} =: Seqs$ be a sequence type.
Let $Op$ denote the Set of sequence operations in general, and
let $Op(Seq)$ denote the Set of operations on sequence type $Seq$.
Examples are prepend, head, get, concat, ... (other parameters omitted for brevity)
Let $time_{Seq}(op), op\in Op(Seq)$ denote the time (in seconds) required to
execute operation $op$ on sequence $Seq$.

Let $Op_{fast}(Seq) \subset Op(Seq)$ be the subset of *fast* operations,
generally they run in $O(1)$ or $O(\log n)$.
Let $Op_{slow}(Seq) := Op(Seq) \setminus Op_{fast}(Seq)$ be the subset of *slow*
operations, they mostly run in $O(n)$.

Examples:
* $head, tail, prepend, get(2), \ldots \in Op_{fast}(List)$
* $last, get, concat, \ldots \in Op_{slow}(List)$
* $get, head, tail, \ldots \in Op_{fast}(Vector)$
* $concat \in Op_{slow}(Vector)$

For $speed\in \{slow, fast\}, let
$$
C_{speed}(Seq) := \max_{op\in Op} \frac{time_{Elwms}(op)}{time_{Seq}(op)}
$$
and let $C_{speed} := \max_{Seq\in Seqs} C_{speed}(Seq).

<!-- thm -->
It holds $\forall op\in Op$, $Seq \in Seqs$, $speed \in \{slow, fast\}$:
$$
time_{Elwms}(op) \le C_{speed}(Seq) \cdot time_{Seq}(op)
$$

## Precise Goals
### Goal 1 ($G1$)
a. $C_{fast} \le 4$
b. $C_{slow} \le 2$

### Goal 2 ($G1$)
Concatenation ($concat$) and subsequencing ($slice$) of two sequences
$e1, e2 \in Elwms$ shall be possible in $O(\log n)$.


## Comments on Goals
Goal 1 means that for operations a Seq was especially optimized for,
the same operation on Elwms may take at most 4 times as long.
And for operations that already take longer ($O(n)$),
Elwms may take only twice as long.

Goal 2 implies that also $insert$ and $remove$ are possible in $O(\log n)$.

