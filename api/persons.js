const people = [
  { id: 1, name: "张三", age: 28, department: "研发部" },
  { id: 2, name: "李四", age: 32, department: "产品部" },
  { id: 3, name: "王五", age: 26, department: "运营部" },
];

module.exports = function handler(request, response) {
  response.setHeader("Content-Type", "application/json; charset=utf-8");
  response.setHeader(
    "Cache-Control",
    "public, s-maxage=300, stale-while-revalidate=600",
  );
  response.status(200).json({
    success: true,
    total: people.length,
    data: people,
  });
};
