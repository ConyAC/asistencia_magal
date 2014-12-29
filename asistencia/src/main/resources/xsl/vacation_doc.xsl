<?xml version='1.0'?>
<!DOCTYPE log [
<!ENTITY aacute "á">
<!ENTITY Aacute "Á">
<!ENTITY eacute "e">
<!ENTITY iacute "í">
<!ENTITY oacute "ó">
<!ENTITY uacute "ú">
]>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">



	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<!-- defines the layout master -->
			<fo:layout-master-set>
				<fo:simple-page-master master-name="first"
					page-height="29.7cm" page-width="21cm" margin-top="1cm"
					margin-bottom="2cm" margin-left="2.5cm" margin-right="2.5cm">
					<fo:region-body margin-top="1cm" />
					<fo:region-before extent="1cm" />
					<fo:region-after extent="1.5cm" />
				</fo:simple-page-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="first">
				<fo:flow flow-name="xsl-region-body">
					<fo:block>
						<html>
							<body style="color:#7379A0">
								<table border="1" cellpadding="0" cellspacing="0"
									style="width:100%;">
									<tr>
										<td style="background-color:#FFF3F7">
											<h1 style="margin:10px;">COMPROBANTE DE FERIADO</h1>
										</td>
										<td style="width:50%" colspan="2">
											<table border="1" cellpadding="0" cellspacing="0"
												style="border-collapse:collapse;width:100%;height:100%">
												<tr style="background-color:#FFF3F7">
													<th>LUGAR</th>
													<th>DIA</th>
													<th>MES</th>
													<th>AÑO</th>
												</tr>
												<tr>
													<td>
														<xsl:value-of select="VacationData/place" />
														<p></p>
													</td>
													<td>
														<xsl:value-of select="VacationData/day" />
														<p></p>
													</td>
													<td>
														<xsl:value-of select="VacationData/month" />
														<p></p>
													</td>
													<td>
														<xsl:value-of select="VacationData/year" />
														<p></p>
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td colspan="3">
											En cumplimiento a las disposiciones legales vigentes se deja
											constancia que a contar de las fechas que se indican, el
											trabajador:
											<br />
											<br />
											DON
											<xsl:value-of select="VacationData/laborerFullname" />
											RUT:
											<xsl:value-of select="VacationData/laborerRut" />
										</td>
									</tr>
									<tr>
										<td colspan="3">
											har&aacute;
											uso
											<xsl:value-of select="VacationData/partOrFull" />
											(indicar si parte o el total) de su
										</td>
									</tr>
									<tr>
										<td>
											Feriado Anual con remuneraci&oacute;n &iacute;ntegra
											de acuerdo al siguiente detalle:
										</td>
										<th style="background-color:#FFF3F7">DIAS</th>
										<th style="background-color:#FFF3F7">VALOR</th>
									</tr>
									<tr>
										<td>
											DESCANSO EFECTIVO ENTRE LAS FECHAS QUE SE INDICAN:
											<br />
											DESDE EL
											<xsl:value-of select="VacationData/from" />
											AL
											<xsl:value-of select="VacationData/to" />
										</td>
										<td>
											<xsl:value-of select="VacationData/days" />
										</td>
										<td>
											<xsl:value-of select="VacationData/value" />
										</td>
									</tr>
									<tr>
										<td>
											FERIADO COMPENSADO
										</td>
										<td>
											<xsl:value-of select="VacationData/days" />
										</td>
										<td>
											<xsl:value-of select="VacationData/amount" />
										</td>
									</tr>
									<tr>
										<td>
											<xsl:value-of select="VacationData/amount_string" />
											(EN LETRAS)
										</td>
										<th style="background-color:#FFF3F7" rowspan="2">TOTAL</th>
										<td rowspan="2">
											<xsl:value-of select="VacationData/total" />
										</td>
									</tr>
									<tr>
										<td align="right">
											pesos.
										</td>
									</tr>
								</table>
								<br />
								<table border="1" cellpadding="0" cellspacing="0"
									style="border-collapse:collapse;width:100%">
									<tr>
										<td>
											<table cellpadding="0" cellspacing="0" border="1"
												style="border-collapse:collapse;width:100%;height:100%">
												<tr style="background-color:#FFF3F7">
													<th>DETALLE DEL FERIADO</th>
													<th>DIAS</th>
												</tr>
												<tr>
													<td>DIAS HABILES</td>
													<td>
														<xsl:value-of select="VacationData/working_days" />
													</td>
												</tr>
												<tr>
													<td>VACACIONES PROGRESIVAS</td>
													<td>
														<xsl:value-of select="VacationData/progresive_vacations" />
													</td>
												</tr>
												<tr>
													<td>DOMINGOS E INHABILES</td>
													<td>
														<xsl:value-of select="VacationData/not_working_days" />
													</td>
												</tr>
												<tr>
													<td>FERIADO FRACCIONADO</td>
													<td>
														<xsl:value-of select="VacationData/holydays" />
													</td>
												</tr>
												<tr>
													<td>SALDO PENDIENTE</td>
													<td>
														<xsl:value-of select="VacationData/pendinds_days" />
													</td>
												</tr>
											</table>
										</td>
										<td>
											<table cellpadding="0" cellspacing="0" border="1"
												style="border-collapse:collapse;width:100%;height:100%">
												<tr style="background-color:#FFF3F7">
													<th>NOMBRE Y FIRMA DEL EMPLEADOR O EMPRESA</th>
												</tr>
												<tr>
													<td>
														<p></p>
													</td>
												</tr>
												<tr>
													<td>
														<p></p>
													</td>
												</tr>
												<tr style="background-color:#FFF3F7">
													<th>FIRMA DEL TRABAJADOR</th>
												</tr>
												<tr>
													<td>
														<p></p>
													</td>
												</tr>
												<tr>
													<td>
														<p></p>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
								<table cellpadding="0" cellspacing="0" border="1" style="width:100%">
									<tr>
										<td style="width:40px"></td>
										<td style="background-color:#FFF3F7">
											<p>
												<b>NOTA:</b>
												Se deja constancia que el cálculo del feriado se ha hecho de
												conformidad a lo dispuesto en el Cap&iacute;tulo
												VII, "Del Feriado Anual y de los Permisos, del Cap&iacute;tulo
												I del C&oacute;digo
												del Trabajo".
											</p>
										</td>
									</tr>
								</table>
							</body>
						</html>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
</xsl:stylesheet>
