[#ftl]
[@b.head/]
  <script src="${base}/static/js/plugins/jquery.table2excel.min.js?v=2"></script>
  <style>
      /* 此样式仅用于浏览器页面效果，Excel不会分离表格边框，不需要此样式 */
      table {
          border-collapse: collapse;
      }
  </style>
  [@b.toolbar title="证书成绩认定报表"]
    bar.addPrint();
    bar.addClose();
  [/@]
  <a>导出报表</a>
  <table class="gridtable excel-table">
    <thead class="gridhead">
      <tr>
        <th>学号</th>
        <th>姓名</th>
        <th>考试科目</th>
        <th>证书成绩</th>
        <th>是否合格</th>
        <th>院系</th>
        <th>证书编号</th>
        <th>录入时间</th>
        <th>认定课程名称（代码）</th>
        <th>认定课程类别</th>
        <th>认定课程学分</th>
        <th>认定课程成绩（显示）</th>
        <th>认定课程绩点</th>
        <th>认定课程修读类别</th>
        <th>认定课程考核方式</th>
        <th>认定课程是否免听</th>
      </tr>
    </thead>
    <tbody>
      [#list externExamGrades?sort_by(["std", "user", "code"]) as externExamGrade]
        [#if externExamGrade.courseGrades?size == 0]
      <tr>
        <td style='vnd.ms-excel.numberformat:@'>${externExamGrade.std.user.code}</td>
        <td>${externExamGrade.std.user.name}</td>
        <td>${externExamGrade.subject.name}</td>
        <td>${(externExamGrade.scoreText)!(externExamGrade.score!)}</th>
        <td>${(externExamGrade.passed)!?string("是","否")}</td>
        <td>${externExamGrade.std.state.department.name}</td>
        <td style='vnd.ms-excel.numberformat:@'>${(externExamGrade.certificate)!"--"}</td>
        <td style='vnd.ms-excel.numberformat:@'>${(externExamGrade.updatedAt?string("yyyy-MM-dd HH:mm:ss"))!"--"}</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
        [#else]
          [#list externExamGrade.courseGrades as courseGrade]
      <tr>
        <td style='vnd.ms-excel.numberformat:@'>${externExamGrade.std.user.code}</td>
        <td>${externExamGrade.std.user.name}</td>
        <td>${externExamGrade.subject.name}</td>
        <td>${(externExamGrade.scoreText)!(externExamGrade.score!)}</th>
        <td>${(externExamGrade.passed)!?string("是","否")}</td>
        <td>${externExamGrade.std.state.department.name}</td>
        <td style='vnd.ms-excel.numberformat:@'>${(externExamGrade.certificate)!"--"}</td>
        <td style='vnd.ms-excel.numberformat:@'>${(externExamGrade.updatedAt?string("yyyy-MM-dd HH:mm:ss"))!"--"}</td>
        <td>${courseGrade.course.name}(${courseGrade.course.code})</td>
        <td>${courseGrade.courseType.name}</td>
        <td>${courseGrade.course.credits}</td>
        <td>${courseGrade.score?string("0.#")}${("（" + courseGrade.scoreText + "）")!}</td>
        <td>${courseGrade.gp?string("0.#")}</td>
        <td>${courseGrade.courseTakeType.name}</td>
        <td>${courseGrade.examMode.name}</td>
        <td>${courseGrade.freeListening?string("是", "否")}</td>
      </tr>
          [/#list]
        [/#if]
      [/#list]
    </tbody>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        [#-- 使用outerHTML属性获取整个table元素的HTML代码（包括<table>标签），然后包装成一个完整的HTML文档，设置charset为urf-8以防止中文乱码 --]
        var html = "<html><head><meta charset='utf-8' /></head><body>" + $(".excel-table")[0].outerHTML + "</body></html>";
        [#-- 实例化一个Blob对象，其构造函数的第一个参数是包含文件内容的数组，第二个参数是包含文件类型属性的对象 --]
        var blob = new Blob([html], { type: "application/vnd.ms-excel" });
        var a = document.getElementsByTagName("a")[0];
        [#-- 利用URL.createObjectURL()方法为a元素生成blob URL --]
        a.href = URL.createObjectURL(blob);
        [#-- 设置文件名 --]
        a.download = "证书成绩认定报表.xls";
      });
    });
  </script>
[@b.foot/]
