[#ftl]
[@b.head/]
  [@b.grid items=externSchools var="externSchool"]
    [@b.gridbar]
      bar.addItem("${b.text("action.new")}", action.add());
      bar.addItem("${b.text("action.modify")}", action.edit());
      bar.addItem("${b.text("action.delete")}", action.remove("确认要删除吗？"));
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="代码" property="code"/]
      [@b.col title="名称" property="name"/]
      [@b.col title="英文名称" property="enName"/]
      [@b.col title="国家地区" property="country.name"/]
      [@b.col title="生效时间" property="beginOn"]${externSchool.beginOn?string("yyyy-MM-dd")}[/@]
      [@b.col title="失效时间" property="endOn"]${(externSchool.endOn?string("yyyy-MM-dd"))!}[/@]
    [/@]
  [/@]
[@b.foot/]
