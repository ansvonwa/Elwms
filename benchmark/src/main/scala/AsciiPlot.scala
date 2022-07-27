import java.io.File
import sys.process._

object AsciiPlot {
  def main(args: Array[String]): Unit = {
//    val rows = preprocess(
//      """
//        |[info] Benchmark                  (seqName)  (size)  Mode  Cnt         Score        Error  Units
//[info] BenchmarkElwms.append100       Elwms       0  avgt   20      3323.041 ±     64.205  ns/op
//[info] BenchmarkElwms.append100       Elwms       1  avgt   20      2017.603 ±      7.240  ns/op
//[info] BenchmarkElwms.append100       Elwms       2  avgt   20      1958.817 ±     32.359  ns/op
//[info] BenchmarkElwms.append100       Elwms       3  avgt   20      1944.736 ±     12.394  ns/op
//[info] BenchmarkElwms.append100       Elwms      10  avgt   20      2002.794 ±      6.460  ns/op
//[info] BenchmarkElwms.append100       Elwms      32  avgt   20      2092.998 ±      7.285  ns/op
//[info] BenchmarkElwms.append100       Elwms      33  avgt   20      2090.436 ±     11.237  ns/op
//[info] BenchmarkElwms.append100       Elwms      64  avgt   20      2097.564 ±      5.735  ns/op
//[info] BenchmarkElwms.append100       Elwms     100  avgt   20      2142.968 ±     47.432  ns/op
//[info] BenchmarkElwms.append100       Elwms    1000  avgt   20      2340.570 ±     32.244  ns/op
//[info] BenchmarkElwms.append100       Elwms   10000  avgt   20      2439.705 ±      6.752  ns/op
//[info] BenchmarkElwms.append100       Elwms  100000  avgt   20      2533.575 ±      9.647  ns/op
//[info] BenchmarkElwms.append100        List       0  avgt   20     27447.255 ±    436.612  ns/op
//[info] BenchmarkElwms.append100        List       1  avgt   20     23961.314 ±    301.100  ns/op
//[info] BenchmarkElwms.append100        List       2  avgt   20     26176.981 ±   1638.868  ns/op
//[info] BenchmarkElwms.append100        List       3  avgt   20     24464.479 ±    516.500  ns/op
//[info] BenchmarkElwms.append100        List      10  avgt   20     27346.528 ±    293.807  ns/op
//[info] BenchmarkElwms.append100        List      32  avgt   20     37288.171 ±    294.979  ns/op
//[info] BenchmarkElwms.append100        List      33  avgt   20     38778.231 ±    479.717  ns/op
//[info] BenchmarkElwms.append100        List      64  avgt   20     56092.462 ±   3584.065  ns/op
//[info] BenchmarkElwms.append100        List     100  avgt   20     77994.496 ±    574.490  ns/op
//[info] BenchmarkElwms.append100        List    1000  avgt   20    540252.428 ±   3670.366  ns/op
//[info] BenchmarkElwms.append100        List   10000  avgt   20   5537848.428 ±  36147.284  ns/op
//[info] BenchmarkElwms.append100        List  100000  avgt   20  59994194.397 ± 422152.563  ns/op
//[info] BenchmarkElwms.append100      Vector       0  avgt   20      3029.704 ±     48.446  ns/op
//[info] BenchmarkElwms.append100      Vector       1  avgt   20      1743.391 ±      6.123  ns/op
//[info] BenchmarkElwms.append100      Vector       2  avgt   20      1752.200 ±      7.289  ns/op
//[info] BenchmarkElwms.append100      Vector       3  avgt   20      1751.064 ±      7.923  ns/op
//[info] BenchmarkElwms.append100      Vector      10  avgt   20      1797.565 ±      6.811  ns/op
//[info] BenchmarkElwms.append100      Vector      32  avgt   20      1837.608 ±      3.984  ns/op
//[info] BenchmarkElwms.append100      Vector      33  avgt   20      1838.012 ±      5.715  ns/op
//[info] BenchmarkElwms.append100      Vector      64  avgt   20      1845.010 ±      5.388  ns/op
//[info] BenchmarkElwms.append100      Vector     100  avgt   20      1841.866 ±      4.806  ns/op
//[info] BenchmarkElwms.append100      Vector    1000  avgt   20      2042.284 ±      5.489  ns/op
//[info] BenchmarkElwms.append100      Vector   10000  avgt   20      2146.450 ±      5.638  ns/op
//[info] BenchmarkElwms.append100      Vector  100000  avgt   20      2237.217 ±      7.987  ns/op
//[info] BenchmarkElwms.prepend100      Elwms       0  avgt   20      3365.112 ±     94.239  ns/op
//[info] BenchmarkElwms.prepend100      Elwms       1  avgt   20      1940.403 ±      6.820  ns/op
//[info] BenchmarkElwms.prepend100      Elwms       2  avgt   20      1955.516 ±      5.252  ns/op
//[info] BenchmarkElwms.prepend100      Elwms       3  avgt   20      1955.630 ±      5.040  ns/op
//[info] BenchmarkElwms.prepend100      Elwms      10  avgt   20      2019.941 ±      6.644  ns/op
//[info] BenchmarkElwms.prepend100      Elwms      32  avgt   20      2089.620 ±      4.996  ns/op
//[info] BenchmarkElwms.prepend100      Elwms      33  avgt   20      2097.762 ±      8.256  ns/op
//[info] BenchmarkElwms.prepend100      Elwms      64  avgt   20      2101.289 ±      9.212  ns/op
//[info] BenchmarkElwms.prepend100      Elwms     100  avgt   20      2097.865 ±      8.422  ns/op
//[info] BenchmarkElwms.prepend100      Elwms    1000  avgt   20      2350.561 ±     10.125  ns/op
//[info] BenchmarkElwms.prepend100      Elwms   10000  avgt   20      2366.421 ±      8.135  ns/op
//[info] BenchmarkElwms.prepend100      Elwms  100000  avgt   20      2513.804 ±      7.742  ns/op
//[info] BenchmarkElwms.prepend100       List       0  avgt   20       391.352 ±      1.300  ns/op
//[info] BenchmarkElwms.prepend100       List       1  avgt   20       386.425 ±      1.096  ns/op
//[info] BenchmarkElwms.prepend100       List       2  avgt   20       382.968 ±      2.499  ns/op
//[info] BenchmarkElwms.prepend100       List       3  avgt   20       384.790 ±      1.258  ns/op
//[info] BenchmarkElwms.prepend100       List      10  avgt   20       384.095 ±      1.825  ns/op
//[info] BenchmarkElwms.prepend100       List      32  avgt   20       384.001 ±      0.628  ns/op
//[info] BenchmarkElwms.prepend100       List      33  avgt   20       382.384 ±      1.917  ns/op
//[info] BenchmarkElwms.prepend100       List      64  avgt   20       382.431 ±      1.548  ns/op
//[info] BenchmarkElwms.prepend100       List     100  avgt   20       386.529 ±      1.294  ns/op
//[info] BenchmarkElwms.prepend100       List    1000  avgt   20       386.899 ±      1.744  ns/op
//[info] BenchmarkElwms.prepend100       List   10000  avgt   20       379.276 ±      1.327  ns/op
//[info] BenchmarkElwms.prepend100       List  100000  avgt   20       383.580 ±      1.178  ns/op
//[info] BenchmarkElwms.prepend100     Vector       0  avgt   20      3037.533 ±    117.046  ns/op
//[info] BenchmarkElwms.prepend100     Vector       1  avgt   20      1744.904 ±      5.243  ns/op
//[info] BenchmarkElwms.prepend100     Vector       2  avgt   20      1751.850 ±      4.347  ns/op
//[info] BenchmarkElwms.prepend100     Vector       3  avgt   20      1750.488 ±      6.748  ns/op
//[info] BenchmarkElwms.prepend100     Vector      10  avgt   20      1793.181 ±      6.911  ns/op
//[info] BenchmarkElwms.prepend100     Vector      32  avgt   20      1854.231 ±     26.527  ns/op
//[info] BenchmarkElwms.prepend100     Vector      33  avgt   20      1828.094 ±      5.078  ns/op
//[info] BenchmarkElwms.prepend100     Vector      64  avgt   20      1827.773 ±      5.349  ns/op
//[info] BenchmarkElwms.prepend100     Vector     100  avgt   20      1833.972 ±      4.503  ns/op
//[info] BenchmarkElwms.prepend100     Vector    1000  avgt   20      2075.016 ±      5.755  ns/op
//[info] BenchmarkElwms.prepend100     Vector   10000  avgt   20      2096.366 ±      5.830  ns/op
//[info] BenchmarkElwms.prepend100     Vector  100000  avgt   20      2250.644 ±      9.923  ns/op
//
//        |[info] BenchmarkElwms.appendVE       Elwms       0  avgt   20      3323.041 ±     64.205  ns/op
//        |[info] BenchmarkElwms.appendVE       Elwms       1  avgt   20      2017.603 ±      7.240  ns/op
//        |[info] BenchmarkElwms.appendVE       Elwms       2  avgt   20      1958.817 ±     32.359  ns/op
//        |[info] BenchmarkElwms.appendVE       Elwms       3  avgt   20      1944.736 ±     12.394  ns/op
//        |[info] BenchmarkElwms.appendVE       Elwms      10  avgt   20      2002.794 ±      6.460  ns/op
//        |[info] BenchmarkElwms.appendVE       Elwms      32  avgt   20      2092.998 ±      7.285  ns/op
//        |[info] BenchmarkElwms.appendVE       Elwms      33  avgt   20      2090.436 ±     11.237  ns/op
//        |[info] BenchmarkElwms.appendVE       Elwms      64  avgt   20      2097.564 ±      5.735  ns/op
//        |[info] BenchmarkElwms.appendVE       Elwms     100  avgt   20      2142.968 ±     47.432  ns/op
//        |[info] BenchmarkElwms.appendVE       Elwms    1000  avgt   20      2340.570 ±     32.244  ns/op
//        |[info] BenchmarkElwms.appendVE       Elwms   10000  avgt   20      2439.705 ±      6.752  ns/op
//        |[info] BenchmarkElwms.appendVE       Elwms  100000  avgt   20      2533.575 ±      9.647  ns/op
//        |[info] BenchmarkElwms.appendVE      Vector       0  avgt   20      3029.704 ±     48.446  ns/op
//        |[info] BenchmarkElwms.appendVE      Vector       1  avgt   20      1743.391 ±      6.123  ns/op
//        |[info] BenchmarkElwms.appendVE      Vector       2  avgt   20      1752.200 ±      7.289  ns/op
//        |[info] BenchmarkElwms.appendVE      Vector       3  avgt   20      1751.064 ±      7.923  ns/op
//        |[info] BenchmarkElwms.appendVE      Vector      10  avgt   20      1797.565 ±      6.811  ns/op
//        |[info] BenchmarkElwms.appendVE      Vector      32  avgt   20      1837.608 ±      3.984  ns/op
//        |[info] BenchmarkElwms.appendVE      Vector      33  avgt   20      1838.012 ±      5.715  ns/op
//        |[info] BenchmarkElwms.appendVE      Vector      64  avgt   20      1845.010 ±      5.388  ns/op
//        |[info] BenchmarkElwms.appendVE      Vector     100  avgt   20      1841.866 ±      4.806  ns/op
//        |[info] BenchmarkElwms.appendVE      Vector    1000  avgt   20      2042.284 ±      5.489  ns/op
//        |[info] BenchmarkElwms.appendVE      Vector   10000  avgt   20      2146.450 ±      5.638  ns/op
//        |[info] BenchmarkElwms.appendVE      Vector  100000  avgt   20      2237.217 ±      7.987  ns/op
//        |
//                |""".stripMargin)

    val filename = args.headOption.getOrElse("jmh-result.csv")
    val rows = preprocessCSV(io.Source.fromFile(s"benchmark/$filename").mkString)

    var width: Int = "tput cols".!!.trim.toInt
    var height: Int = "tput lines".!!.trim.toInt
    if (width == 51 && height == 56) {
      width = 200
      height = 14
    }
    plotAll(rows, width, height - 1)
  }

  case class DataRow(plotName: String, seqName: String, size: Int, time: Float, deviation: Float)

  def preprocessCSV(csv: String): Seq[DataRow] = {
    def strip(s: String) = s.replaceAll("^\"|\"$", "")
    csv.linesIterator.toSeq
      .filter(_.contains("ns/op"))
      .map(_.split(","))
      .map(arr => DataRow(strip(arr(0)), strip(arr(7)), arr(8).toInt, arr(4).toFloat, arr(5).toFloat))
  }

  def preprocess(raw: String): Seq[DataRow] = {
    raw.linesIterator.toSeq
      .filter(_.contains("ns/op"))
      .map(_.replaceAll(" +", " "))
      .map(_.split(" "))
      .map(arr => DataRow(arr(1), arr(2), arr(3).toInt, arr(6).toFloat, arr(8).toFloat))
  }

  def plotAll(dataRows: Seq[DataRow], width: Int, height: Int): Unit = {
    dataRows.groupBy(_.plotName).foreach(x => plot(x._2, width, height))
  }

  def plot(dataRows: Seq[DataRow], width: Int, height: Int): Unit = {
    println(s"printing ${dataRows.head.plotName} in $width x $height")

    def formatSmall(f: Double, length: Int, centered: Boolean = true): String = {
      def pad(s: String): String = {
        if (s.length > length) return "#" * length
        val left = if (!centered) 0 else (length - s.length) / 2
        val right = length - s.length - left
        (" " * left) + s + (" " * right)
      }

      if (f.toInt.toString.length <= length) { // ok, can just cut off from right
        pad(f"$f%15.15f".toString.replaceFirst("\\.?0+$", "").take(length))
      } else {
        val exp = f.toInt.toString.length - 1
        val expStr = "E" + exp.toString
        val prefStr = (f / math.pow(10, exp)).toString
        pad(prefStr.take(length - expStr.length) + expStr)
      }
    }

    val (minTime, maxTime, timeLabelWidth) = {
      val times = dataRows.map(_.time)
      (times.min, times.max, times.map(d => formatSmall(d, 8).trim.length).max)
    }
    val plotWidth = width - timeLabelWidth - 2
    val plotHeight = height - 2

    val sizes = dataRows.groupBy(_.size).keys.toSeq.sorted

    val dLog = math.log(maxTime) - math.log(minTime)

    val timeLabels = {
      val num = ((plotHeight + 1) / 2)
      val positions =
        (0 until num)
          .map(line => math.round(line * (plotHeight + 1.0) / num))
          .map(_.toInt)
      positions
        .map(pos => pos -> math.exp(math.log(minTime) + (pos / (plotHeight - 1.0)) * dLog))
        .toMap
    }

    val dots = dataRows.map { row =>
      val chr = row.seqName.head
      val color = chr match {
        case 'E' => Console.GREEN
        case 'L' => Console.RED
        case 'V' => Console.BLUE
      }
      val x = ((sizes.indexOf(row.size) + 0.5) * plotWidth / sizes.size).toInt + (chr match {
        case 'L' => -1
        case 'V' => 0
        case 'E' => 1
      })
      val y = ((math.log(row.time) - math.log(minTime)) / dLog * plotHeight * 0.999).toInt
      (x, y) -> (color + chr + Console.RESET)
    }.toMap

    val background = " "
    for (line <- (0 until height).reverse) {
      val leftLabel = (timeLabels.get(line - 2) match {
        case Some(value) => formatSmall(value, timeLabelWidth, false)
        case None => background * timeLabelWidth
      }) + " " + (line match {
        case 0 => " "
        case 1 => "+"
        case top if top == height - 1 => "^"
        case _ => "|"
      })
      print(leftLabel)

      line match {
        case 0 => // lower label
          val num = sizes.size
          val lowerLabel =
            sizes.zipWithIndex
              .map { case (label, index) =>
                val labelWidth = (index + 1) * plotWidth / num - index * plotWidth / num
                formatSmall(label, labelWidth - 2)
              }
              .mkString(" ", "  ", " ")
          println(lowerLabel)
        case 1 => println("-" * (plotWidth - 1) + ">")
        case _ =>
          for (row <- (0 until plotWidth)) {
            val chr = dots.get((row, line - 2)) match {
              case Some(value) => value
              case None => ' '
            }
            print(chr)
          }
          println("")
      }
    }
  }
}
