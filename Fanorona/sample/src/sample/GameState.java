@Override
public GameState move(GameState state) 
{
    int alpha = -INFINITY;
    int beta = INFINITY;
    int bestScore = -Integer.MAX_VALUE;
    GameTreeNode gameTreeRoot = new GameTreeNode(state);
    GameState bestMove = null;
    for(GameTreeNode child: gameTreeRoot.getChildren())
    {
        if(bestMove == null)
        {
            bestMove = child.getState();
        }
        alpha = Math.max(alpha, miniMax(child, plyDepth - 1, alpha, beta));
        if(alpha > bestScore)
        {
            bestMove = child.getState();
            bestScore = alpha;
        }
    }
    return bestMove;
}

private int miniMax(GameTreeNode currentNode, int depth, int alpha, int beta) 
{
    if(depth <= 0 || terminalNode(currentNode.getState())) 
    {
        return getHeuristic(currentNode.getState());
    }
    if(currentNode.getState().getCurrentPlayer().equals(selfColor))
    {
        for(GameTreeNode child: currentNode.getChildren())
        {
            alpha = Math.max(alpha, miniMax(child, depth - 1, alpha, beta));

            if(alpha >= beta)
            {
                return beta;
            }
        }
        return alpha;
    }
    else
    {
        for(GameTreeNode child: currentNode.getChildren())
        {
            beta = Math.min(beta, miniMax(child, depth - 1, alpha, beta));

            if(alpha >= beta)
            {
                return alpha;
            }
        }
        return beta;
    }
}
//Checks to see if the node is terminal
private boolean terminalNode(GameState state)
{
if(state.getStatus().equals(win) || state.getStatus().equals(lose) || state.getStatus().equals(draw))
    {
        return true;
    }
    else
    {
        return false;
    }
}
}