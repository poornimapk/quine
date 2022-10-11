package com.thatdot.quine.graph.behavior

import com.thatdot.quine.graph.BaseNodeActorView
import com.thatdot.quine.model.EdgeDirection.{Incoming, Outgoing, Undirected}
import com.thatdot.quine.model.{DomainEdge, DomainNodeEquiv, HalfEdge, SingleBranch}

trait DomainNodeTests extends BaseNodeActorView {

  private[this] def localPropsMatch(testNodeEquiv: DomainNodeEquiv): Boolean =
    testNodeEquiv.localProps forall { case (s, (compFunc, testPropVal)) =>
      compFunc(testPropVal, properties.get(s))
    }

  private[this] def hasCircularEdges(testNodeEquiv: DomainNodeEquiv): Boolean =
    testNodeEquiv.circularEdges.forall(circTest =>
      if (circTest._2) { // isDirected:
        edges.contains(HalfEdge(circTest._1, Outgoing, qid)) &&
        edges.contains(HalfEdge(circTest._1, Incoming, qid))
      } else {
        edges.contains(HalfEdge(circTest._1, Undirected, qid))
      }
    )

  private[this] def hasGenericEdges(requiredEdges: Set[DomainEdge]): Boolean =
    edges.hasUniqueGenEdges(requiredEdges, qid)

  protected[this] def localTestBranch(testBranch: SingleBranch): Boolean =
    testBranch.identification.forall(_ == qid) && localPropsMatch(testBranch.domainNodeEquiv) && hasCircularEdges(
      testBranch.domainNodeEquiv
    ) && hasGenericEdges(testBranch.nextBranches.toSet[DomainEdge])
}
