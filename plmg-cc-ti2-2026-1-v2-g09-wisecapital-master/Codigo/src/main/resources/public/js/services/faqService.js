export async function listarFaqs() {
  const response = await fetch("/faq/listar");

  return await response.json();
}
