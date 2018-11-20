[#ftl]
[#assign divisionOptions]<option value="">${Parameters["empty"]!"全国"}</option>[#list divisions?sort_by(["code"]) as division]<option value="${division.id}">${ division.code[0..1] + '-' + division.name }</option>[/#list][/#assign]
[#assign examTimeOptions]<option value="">${Parameters["empty"]!"全部"}</option>[#list examTimes?sort_by(["code"]) as examTime]<option value="${examTime.id}">${examTime.name}</option>[/#list][/#assign]
{
  "divisionOptions": "${divisionOptions?js_string}",
  "examTimeOptions": "${examTimeOptions?js_string}"
}
