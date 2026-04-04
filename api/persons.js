const people = [
  { id: 1, name: "张三", age: 28, department: "研发部" },
  { id: 2, name: "李四", age: 32, department: "产品部" },
  { id: 3, name: "王五", age: 26, department: "运营部" },
];

function parseQuery(request) {
  if (request.query) {
    return request.query;
  }

  const url = new URL(request.url || "http://localhost/persons");
  return Object.fromEntries(url.searchParams.entries());
}

function matchesName(person, name) {
  if (!name) {
    return true;
  }
  return person.name.toLowerCase().includes(name.trim().toLowerCase());
}

function matchesDepartment(person, department) {
  if (!department) {
    return true;
  }
  return person.department === department.trim();
}

function resolveSorter(sortBy, order) {
  const direction = order === "desc" ? -1 : 1;

  return (left, right) => {
    const leftValue = left[sortBy] ?? left.id;
    const rightValue = right[sortBy] ?? right.id;

    if (typeof leftValue === "string" && typeof rightValue === "string") {
      return leftValue.localeCompare(rightValue, "zh-CN") * direction;
    }

    return (leftValue - rightValue) * direction;
  };
}

function summarize(data) {
  const totalAge = data.reduce((sum, person) => sum + person.age, 0);
  const youngestAge = data.length
    ? Math.min(...data.map((person) => person.age))
    : 0;
  const oldestAge = data.length
    ? Math.max(...data.map((person) => person.age))
    : 0;
  const averageAge = data.length
    ? Math.round((totalAge / data.length) * 10) / 10
    : 0;

  return {
    averageAge,
    youngestAge,
    oldestAge,
    departments: [...new Set(data.map((person) => person.department))],
  };
}

module.exports = function handler(request, response) {
  const query = parseQuery(request);
  const name = query.name?.trim() || "";
  const department = query.department?.trim() || "";
  const minAge = Number(query.minAge);
  const maxAge = Number(query.maxAge);
  const sortBy = ["id", "name", "age"].includes(query.sortBy)
    ? query.sortBy
    : "id";
  const order = query.order === "desc" ? "desc" : "asc";
  const limit = Number(query.limit);

  let data = people
    .filter((person) => matchesName(person, name))
    .filter((person) => matchesDepartment(person, department))
    .filter((person) => Number.isNaN(minAge) || person.age >= minAge)
    .filter((person) => Number.isNaN(maxAge) || person.age <= maxAge)
    .sort(resolveSorter(sortBy, order));

  if (!Number.isNaN(limit) && limit > 0) {
    data = data.slice(0, limit);
  }

  response.setHeader("Content-Type", "application/json; charset=utf-8");
  response.setHeader(
    "Cache-Control",
    "public, s-maxage=300, stale-while-revalidate=600",
  );
  response.status(200).json({
    success: true,
    total: data.length,
    filters: {
      name,
      department,
      minAge: Number.isNaN(minAge) ? null : minAge,
      maxAge: Number.isNaN(maxAge) ? null : maxAge,
      sortBy,
      order,
      limit: Number.isNaN(limit) || limit <= 0 ? null : limit,
    },
    stats: summarize(data),
    data,
  });
};
