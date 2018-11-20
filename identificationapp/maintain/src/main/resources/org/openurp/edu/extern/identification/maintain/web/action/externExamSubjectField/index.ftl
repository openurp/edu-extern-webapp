[#ftl]
[@b.head/]
  [@b.toolbar title="证书大类数据来源配置－字段配置管理"]
    bar.addItem("返回", function() {
      bg.Go("${b.url("extern-exam-subject-setting")}", "main");
    }, "backward.png");
  [/@]
  [@b.div id="fields"/]
  <script>
    $(function() {
      $(document).ready(function() {
        bg.Go("${b.url("!search")}", "fields");
      });
    });
  </script>
[@b.foot/]
