[#ftl]
[@b.head/]
  [@b.grid items=examSubjects var="examSubject"]
    [@b.gridbar]
      bar.addItem("${b.text("action.new")}", action.add());
      bar.addItem("${b.text("action.modify")}", action.edit());
      bar.addItem("${b.text("action.delete")}", action.remove("确认要删除吗？"));
      function importParam() {
        action.addParam("file", "template/excel/examSubjectTemplate.xls");
        action.addParam("display", "证书大类导入模板");
        action.addParam("importTitle", "证书大类导入");
      }
      var menuBar = bar.addMenu("导入", function() {
        importParam();
        bg.form.submit(action.getForm(), "${b.url("!importForm")}", "examSubjects");
      });
      menuBar.addItem("导入模板下载", function() {
        importParam();
        bg.form.submit(action.getForm(), "${b.url("!downloadTemplate")}", "_self");
      }, "${base}/static/images/action/download.gif");
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="代码" property="code"/]
      [@b.col title="名称" property="name"/]
      [@b.col title="启动日期" property="beginOn"]${examSubject.beginOn?string("yyyy-MM-dd")}[/@]
      [@b.col title="截止日期" property="endOn"]${(examSubject.endOn?string("yyyy-MM-dd"))!}[/@]
    [/@]
  [/@]
  [@b.form name="examExamSubjectListForm" action="!search" target="_self"]
  [/@]
[@b.foot/]
