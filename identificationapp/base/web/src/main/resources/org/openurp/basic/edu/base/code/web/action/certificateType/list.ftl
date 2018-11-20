[#ftl]
[@b.head/]
  [@b.grid items=types var="type"]
    [@b.gridbar]
      bar.addItem("${b.text("action.new")}", action.add());
      bar.addItem("${b.text("action.modify")}", action.edit());
      bar.addItem("${b.text("action.delete")}", action.remove("确认要删除吗？"));
      function importParam() {
        action.addParam("file", "template/excel/certificateTypeTemplate.xls");
        action.addParam("display", "证书子类／考试科目导入模板");
        action.addParam("importTitle", "证书子类／考试科目导入");
      }
      var menuBar = bar.addMenu("导入", function() {
        importParam();
        bg.form.submit(action.getForm(), "${b.url("!importForm")}", "types");
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
      [@b.col title="大类" property="examSubject.name"/]
      [@b.col title="启动日期" property="beginOn" width="100px"]${type.beginOn?string("yyyy-MM-dd")}[/@]
      [@b.col title="截止日期" property="endOn" width="100px"]${(type.endOn?string("yyyy-MM-dd"))!}[/@]
    [/@]
  [/@]
[@b.foot/]
