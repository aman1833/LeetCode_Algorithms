// https://leetcode.com/problems/cat-and-mouse/
import java.util.*;
public class CatAndMouse {

  private final int CAT = 2, MOUSE = 1, DRAW = 0, PLAYER_CAT = 1, PLAYER_MOUSE = 0;
  private Queue<State> queue = new ArrayDeque<>();

  class State {
    int catPos, mPos, player, color;

    State(int mPos, int catPos, int player, int color) {
      this.catPos = catPos;
      this.mPos = mPos;
      this.player = player;
      this.color = color;
    }
  }
  public static void main(String[] args) {
    int[][] graph = {{2, 5}, {3}, {0, 4, 5}, {1, 4, 5}, {2, 3}, {0, 2, 3}};
    System.out.println(new CatAndMouse().catMouseGame(graph));
  }

  public int catMouseGame(int[][] graph) {
    int[][][] color = new int[graph.length][graph.length][2];
    int[][][] degree = new int[graph.length][graph.length][2];

    for (int i = 1; i < graph.length; i++) {
      for (int p = 0; p < 2; p++) {
        color[0][i][p] = MOUSE;
        queue.offer(new State(0, i, p, MOUSE));
        color[i][i][p] = CAT;
        queue.offer(new State(i, i, p, CAT));
      }
    }

    for (int m = 0; m < graph.length; m++) {
      for (int c = 1; c < graph.length; c++) {
        degree[m][c][0] = graph[m].length;
        degree[m][c][1] = graph[c].length;
        for (int v : graph[c]) {
          if (v == 0) {
            degree[m][c][1]--;
            break;
          }
        }
      }
    }

    while (!queue.isEmpty()) {
      State current = queue.poll();
      List<State> parents = getParents(graph, current, color);
      if (color[current.mPos][current.catPos][current.player] == CAT) {
        enqueue(queue, parents, PLAYER_CAT, CAT, color, degree);
      } else {
        enqueue(queue, parents, PLAYER_MOUSE, MOUSE, color, degree);
      }
    }
    return color[1][2][0];
  }

  private void enqueue(
      Queue<State> queue,
      List<State> parents,
      int player,
      int col,
      int[][][] color,
      int[][][] degree) {
    for (State parent : parents) {
      if (color[parent.mPos][parent.catPos][parent.player] == DRAW) {
        if (parent.player == player) {
          color[parent.mPos][parent.catPos][parent.player] = col;
          queue.offer(new State(parent.mPos, parent.catPos, parent.player, col));
        } else {
          int currDegree = --degree[parent.mPos][parent.catPos][parent.player];
          if (currDegree == 0) {
            color[parent.mPos][parent.catPos][parent.player] = col;
            queue.offer(new State(parent.mPos, parent.catPos, parent.player, col));
          }
        }
      }
    }
  }

  private List<State> getParents(int[][] graph, State current, int[][][] color) {
    int player = current.player;
    int[] positions;
    List<State> list = new ArrayList<>();
    if (player == PLAYER_MOUSE) {
      positions = graph[current.catPos];
      for (int pos : positions) {
        if (pos == 0) continue;
        if (color[current.mPos][pos][PLAYER_CAT] == DRAW) {
          list.add(new State(current.mPos, pos, PLAYER_CAT, DRAW));
        }
      }
    } else {
      positions = graph[current.mPos];
      for (int pos : positions) {
        if (color[pos][current.catPos][PLAYER_MOUSE] == DRAW) {
          list.add(new State(pos, current.catPos, PLAYER_MOUSE, DRAW));
        }
      }
    }
    return list;
  }
}
