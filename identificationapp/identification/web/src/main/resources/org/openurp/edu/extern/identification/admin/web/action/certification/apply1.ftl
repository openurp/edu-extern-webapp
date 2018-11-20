[#ftl]
[@b.head/]
  [@b.toolbar title = "学生证书申请－<span style=\"color:blue\">第<span style=\"padding-left: 3px; padding-right: 3px\">1</span>步：输入学号</span>"]
    bar.addItem("取消返回", function() {
      bg.form.submit(document.searchForm);
    }, "backward.png");
  [/@]
  [@b.form name="certificationForm" action="!apply" target="certifications" theme="list"]
    [@b.textfield label="学号" name="no" value="" required="true" style="width: 200px"/]
    [@b.validity]
      $("[name=no]", document.certificationForm).require().assert(function() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!apply1CheckAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "no": document.certificationForm["no"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }, "学号不存在！！！");
    [/@]
    [@b.formfoot]
      [@b.submit id="apply1" value="下一步"/]
    [/@]
  [/@]
[@b.foot/]
