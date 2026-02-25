package com.example.freelanceplatformspringapp.Complaints.Services;

import com.example.freelanceplatformspringapp.Complaints.Entity.Response;
import com.example.freelanceplatformspringapp.Complaints.Repository.ResponseRespository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResponseService implements IResponseInterface {
    private final ResponseRespository rr;

    @Override
    public Response addResponse(Response response) {return rr.save(response);}

    @Override
    public Response updateResponse(Response response) {return rr.save(response);}

    @Override
    public List<Response> getResponseList() {return  rr.findAll();}

    @Override
    public Optional<Response> getResponseById(Long id) {return rr.findById(id);}

    @Override
    public void deleteResponse(Long id) {rr.deleteById(id);}
}