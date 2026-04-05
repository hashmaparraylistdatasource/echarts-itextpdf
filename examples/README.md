# Examples

The example applications live outside the main library source tree so the published library
artifact stays focused on reusable code.

That includes demo support helpers such as `LocalNodeRenderService`, which are intentionally
kept out of the main runtime artifact.

## Run the sample PDF demo

```powershell
cd E:\myApp\cims-export\echarts-itextpdf
$env:JAVA_HOME=$env:JAVA8_HOME
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn -Pexamples compile exec:java "-Dexec.mainClass=io.github.echartsitext.example.SamplePdfMain"
```

## Run the gallery PDF demo

```powershell
mvn -Pexamples compile exec:java "-Dexec.mainClass=io.github.echartsitext.example.GalleryPdfMain"
```

Both demos will auto-start the bundled local render service and write output PDF files under
`examples/output/`.

The gallery now includes:

- line
- pie / donut
- radar
- scatter
- heatmap
- candlestick
- funnel
- 3D bar
